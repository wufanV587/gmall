package com.atguigu.gmall.search.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Query;
import com.atguigu.gmall.search.vo.GoodsVO;
import com.atguigu.gmall.search.vo.SearchParamVO;
import com.atguigu.gmall.search.vo.SearchResponse;
import com.atguigu.gmall.search.vo.SearchResponseAttrVO;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.ChildrenAggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SearchService {

    @Autowired
    private JestClient jestClient;

    public SearchResponse search(SearchParamVO searchParamVO) {

        try {

            String dsl = buildDSL(searchParamVO);
            System.out.println(dsl);
            Search search = new Search.Builder(dsl).addIndex("goods").addType("info").build();
            SearchResult searchResult = this.jestClient.execute(search);
            SearchResponse response = parseResoult(searchResult);
            //分页参数
            response.setPageSize(searchParamVO.getPageSize());
            response.setPageNum(searchParamVO.getPageNum());
            response.setTotal(searchResult.getTotal());

            System.out.println(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //解析结果集
    private SearchResponse parseResoult(SearchResult result) {
        SearchResponse response = new SearchResponse();
        //获取所有聚合
        MetricAggregation aggregations = result.getAggregations();
        //解析品牌的聚合结果集
        //获取品牌聚合
        TermsAggregation brandAgg = aggregations.getTermsAggregation("brandAgg");
        //获取品牌聚合集的所有桶
        List<TermsAggregation.Entry> buckets = brandAgg.getBuckets();
        //判断品牌聚合机是否为空
        if (!CollectionUtils.isEmpty(buckets)) {
            //初始化品牌VO对象
            SearchResponseAttrVO attrVO = new SearchResponseAttrVO();
            attrVO.setName("品牌"); //写死品牌聚合名称
            List<String> brandValues = buckets.stream().map(bucket -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", bucket.getKeyAsString());
                //获取品牌id桶中自聚合（品牌名称）
                TermsAggregation brandNameAgg = bucket.getTermsAggregation("brandNameAgg");
                        map.put("name", brandNameAgg.getBuckets().get(0).getKeyAsString());
                        return JSON.toJSONString(map);
                    }
            ).collect(Collectors.toList());
            //设置品牌所有聚合值
            attrVO.setValue(brandValues);
            response.setBrand(attrVO);
        }
        //解析分类的聚合结果集
        TermsAggregation categoryAgg = aggregations.getTermsAggregation("categoryAgg");
        List<TermsAggregation.Entry> catBbuckets = categoryAgg.getBuckets();
        if (!CollectionUtils.isEmpty(catBbuckets)) {
            SearchResponseAttrVO categoryVO = new SearchResponseAttrVO();
            categoryVO.setName("分类");
            List<String> categoryVlaues = catBbuckets.stream().map(bucket -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", bucket.getKeyAsString());
                TermsAggregation categoryNameAgg = bucket.getTermsAggregation("categoryNameAgg");
                map.put("name", categoryNameAgg.getBuckets().get(0).getKeyAsString());
                return JSON.toJSONString(map);
            }).collect(Collectors.toList());
            categoryVO.setValue(categoryVlaues);
            response.setCatelog(categoryVO);
         }

            //解析搜索属性的聚合结果集
            ChildrenAggregation attrAgg = aggregations.getChildrenAggregation("attrAgg");
            TermsAggregation attrIdAgg = attrAgg.getTermsAggregation("attrIdAgg");
            List<SearchResponseAttrVO> attrVOS = attrIdAgg.getBuckets().stream().map(bucket -> {
                SearchResponseAttrVO attrVO = new SearchResponseAttrVO();
                attrVO.setProductAttributeId(Long.valueOf(bucket.getKeyAsString()));
                // 获取搜索属性的子聚合（搜索属性名）
                TermsAggregation attrNameAgg = bucket.getTermsAggregation("attrNameAgg");
                attrVO.setName(attrNameAgg.getBuckets().get(0).getKeyAsString());
                // 获取搜索属性的子聚合（搜索属性值）
                TermsAggregation attrValueAgg = bucket.getTermsAggregation("attrValueAgg");
                List<String> values = attrValueAgg.getBuckets().stream().map(bucket1 -> bucket1.getKeyAsString()).collect(Collectors.toList());
                attrVO.setValue(values);
                return attrVO;
            }).collect(Collectors.toList());
        response.setAttrs(attrVOS);

        // 解析商品列表的结果集
        List<GoodsVO> goodsVOS = result.getSourceAsObjectList(GoodsVO.class, false);
        response.setProducts(goodsVOS);

        return response;
        }

        //商品搜索
        private String buildDSL (SearchParamVO searchParamVO){
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            //1.构建查询和过滤条件
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //构建查询条件
            String keyword = searchParamVO.getKeyword();
            if (StringUtils.isNotEmpty(keyword)) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("name", keyword).operator(Operator.AND));
            }
            //构建过滤条件
            //品牌
            String[] brands = searchParamVO.getBrand();
            if (ArrayUtils.isNotEmpty(brands)) {
                boolQueryBuilder.filter(QueryBuilders.termsQuery("name", brands));
            }
            //分类
            String[] catelog3 = searchParamVO.getCatelog3();
            if (ArrayUtils.isNotEmpty(catelog3)) {
                boolQueryBuilder.filter(QueryBuilders.termsQuery("productCategoryId", catelog3));
            }

            //搜索规格属性的过滤
            String[] props = searchParamVO.getProps();
            if (ArrayUtils.isNotEmpty(props)) {
                for (String prop : props) {
                    String[] attr = StringUtils.split(prop, ":");
                    if (attr != null && attr.length == 2) {
                        BoolQueryBuilder propBoolQuery = QueryBuilders.boolQuery();
                        propBoolQuery.must(QueryBuilders.termQuery("attrValueList.productAttributeId", attr[0]));
                        String[] values = StringUtils.split(attr[1], "-");
                        propBoolQuery.must(QueryBuilders.termsQuery("attrValueList.value", values));
                        boolQueryBuilder.filter(QueryBuilders.nestedQuery("attrValueList", propBoolQuery, ScoreMode.None));
                    }
                }
            }
            searchSourceBuilder.query(boolQueryBuilder);
            //2.完成分页的构建
            Integer pageNum = searchParamVO.getPageNum();
            Integer pageSize = searchParamVO.getPageSize();
            searchSourceBuilder.from((pageNum - 1) * pageSize);
            searchSourceBuilder.size(pageSize);
            // 3.排序的构建
            String order = searchParamVO.getOrder();
            if (StringUtils.isNotEmpty(order)) {
                String[] orders = StringUtils.split(order, ":");
                if (orders != null && orders.length == 2) {
                    SortOrder sortOrder = StringUtils.equals("asc", orders[1]) ? SortOrder.ASC : SortOrder.DESC;
                    switch (orders[0]) {
                        case "0":
                            searchSourceBuilder.sort("_score", sortOrder);
                            break;
                        case "1":
                            searchSourceBuilder.sort("sale", sortOrder);
                            break;
                        case "2":
                            searchSourceBuilder.sort("price", sortOrder);
                            break;
                        default:
                            break;
                    }
                }
            }
            // 4.完成高亮的构建
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("name");
            highlightBuilder.preTags("<font color = red>");
            highlightBuilder.postTags("</font>");
            searchSourceBuilder.highlighter(highlightBuilder);
            //5.完成聚合条件的构建
            //品牌
            searchSourceBuilder.aggregation(AggregationBuilders.terms("brandAgg").field("brandId")
                    .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName")));

            //分类
            searchSourceBuilder.aggregation(
                    AggregationBuilders.terms("categoryAgg").field("productCategoryId")
                            .subAggregation(AggregationBuilders.terms("categoryNameAgg").field("productCategoryName")));

            //搜索属性
            searchSourceBuilder.aggregation(
                    AggregationBuilders.nested("attrAgg", "attrValueList")
                            .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrValueList.productAttributeId")
                                    .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrValueList.name"))
                                    .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrValueList.value"))
                            )
            );
            return searchSourceBuilder.toString();
        }

}
