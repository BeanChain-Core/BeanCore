package com.beanpack.Utils;

import com.beanpack.TXs.TX;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



public class MetaHelper {

    /**
     * MetaHelper unpacks TX metadata into JsonNode
     * 
     * @param tx any TX with needed metadata
     * @return JsonNode of the TX metadata
     * @throws Exception If the metadata is not valid JSON.
     */
    public static JsonNode getMetaNode(TX tx) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(tx.getMeta());
    }
    
}
