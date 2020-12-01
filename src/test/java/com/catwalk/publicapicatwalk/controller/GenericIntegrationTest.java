package com.catwalk.publicapicatwalk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class GenericIntegrationTest {

    @Autowired
    protected ObjectMapper objectMapper;

    public MockHttpServletRequestBuilder createGetRequest(String sPath) throws Exception {
        return get(sPath);
    }

    public MockHttpServletRequestBuilder createGetRequest(String sPath, String sBearerToken) throws Exception {
        return get(sPath).header("Authorization", "Bearer " + sBearerToken);
    }

    public MockHttpServletRequestBuilder createPostRequest(String sPath) throws Exception {
        return post(sPath);
    }

    public MockHttpServletRequestBuilder createPostRequest(String sPath, Object oData) throws Exception {
        return post(sPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oData));
    }

    public MockHttpServletRequestBuilder createPostRequest(String sPath, Object oData, String sBearerToken) throws Exception {
        return post(sPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oData))
                .header("Authorization", "Bearer " + sBearerToken);
    }

    public MockHttpServletRequestBuilder createPatchRequest(String sPath, Object oData) throws Exception {
        return patch(sPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oData));
    }

    public MockHttpServletRequestBuilder createPatchRequest(String sPath, Object oData, String sBearerToken) throws Exception {
        return patch(sPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oData))
                .header("Authorization", "Bearer " + sBearerToken);
    }

    public MockHttpServletRequestBuilder createPutRequest(String sPath, Object oData) throws Exception {
        return put(sPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oData));
    }

    public MockHttpServletRequestBuilder createPutRequest(String sPath, Object oData, String sBearerToken) throws Exception {
        return put(sPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oData))
                .header("Authorization", "Bearer " + sBearerToken);
    }

    public MockHttpServletRequestBuilder createDeleteRequest(String sPath) throws Exception {
        return delete(sPath)
                .contentType(MediaType.APPLICATION_JSON);
    }

    public MockHttpServletRequestBuilder createDeleteRequest(String sPath, String sBearerToken) throws Exception {
        return delete(sPath)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + sBearerToken);
    }

}
