package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jdk.nashorn.internal.parser.JSONParser;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.elasticsearch.index.query.QueryBuilders.geoDistanceQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * Created by Hafiz on 11/6/2016.
 */
@Configuration
public class ElasticsearchConfiguration {
    @Bean
    public Client client() throws UnknownHostException {
        // The following settings aren't strictly necessary, because the default cluster name is "elasticsearch".
        //Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
        TransportClient client;
        String index = "us_large_cities";
        client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        searchKeyWord(client, index);
        geoSearch(client, index);
        return client;
    }

    private static void searchKeyWord(TransportClient client, String index) {
        ObjectMapper mapper = new ObjectMapper();
        SearchResponse response = client.prepareSearch(index)
                .setQuery(QueryBuilders.matchAllQuery())
                .execute()
                .actionGet();
        System.out.println("searchKeyWord : " + response);
    }

    private static void geoSearch(TransportClient client, String index) {
        QueryBuilder query = QueryBuilders.geoDistanceQuery("location").point(37.9357576, -122.3477486).distance(200, DistanceUnit.KILOMETERS);
        SearchResponse response = client.prepareSearch(index)
                .setQuery(query)
                .setFrom(0)
                .setSize(15)
                .addSort(SortBuilders.geoDistanceSort("location", 37.9357576, -122.3477486).order(SortOrder.ASC))
                .execute()
                .actionGet();
        System.out.println("searchLocation : " + response);
    }
}
