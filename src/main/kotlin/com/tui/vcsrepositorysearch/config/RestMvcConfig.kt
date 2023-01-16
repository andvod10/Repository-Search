package com.tui.vcsrepositorysearch.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor
import org.springframework.web.accept.ContentNegotiationManager
import org.springframework.web.accept.HeaderContentNegotiationStrategy
import org.springframework.web.accept.FixedContentNegotiationStrategy
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver
import java.util.ArrayList

@Configuration
class RestMvcConfig @Autowired internal constructor(
    private val applicationContext: ApplicationContext
) : WebMvcConfigurer {
    private val defaultContentType = MediaType.APPLICATION_JSON

    /**
     * Registers the json converter to process the accept type application/json and configures
     * the object mapper to be used with jackson.
     *
     * @return the configured json converter
     */
    @Bean
    fun mappingJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter {
        val builder = Jackson2ObjectMapperBuilder()
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        builder.serializationInclusion(JsonInclude.Include.NON_NULL)
        return MappingJackson2HttpMessageConverter(builder.build())
    }

    /**
     * Redefines default converter list to only to json message converter.
     */
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>?>) {
        converters.add(mappingJackson2HttpMessageConverter())
    }

    /**
     * Redefines request mapping handler mapping.
     *
     * @return the configured request mapping handler mapping
     */
    @Bean
    fun requestMappingHandlerMapping(): RequestMappingHandlerMapping {
        return RequestMappingHandlerMapping()
    }

    /**
     * Redefines request mapping handler adapter to use json converter, handle method return resolver
     * to use http entity and request response method processor with content negotiation strategy of
     * to accept content type from request accept header; fallback will be to serve fixed content of media type application json.
     *
     * @return the configured request mapping handler adapter
     */
    @Bean
    fun requestMappingHandlerAdapter(): RequestMappingHandlerAdapter {
        val requestMappingHandlerAdapter = RequestMappingHandlerAdapter()
        val messageConverters: MutableList<HttpMessageConverter<*>> = ArrayList()
        messageConverters.add(mappingJackson2HttpMessageConverter())
        requestMappingHandlerAdapter.messageConverters = messageConverters
        val returnValueHandlers: MutableList<HandlerMethodReturnValueHandler> = ArrayList()
        returnValueHandlers
            .add(HttpEntityMethodProcessor(messageConverters, mvcContentNegotiationManager()))
        returnValueHandlers
            .add(RequestResponseBodyMethodProcessor(messageConverters, mvcContentNegotiationManager()))
        requestMappingHandlerAdapter.returnValueHandlers = returnValueHandlers
        return requestMappingHandlerAdapter
    }

    /**
     * Redefines content negotiation manager content negotiation strategy of to accept content type from request accept header;
     * fallback will be to serve fixed content of media type application json.
     *
     * @return the configured content negotiation manager
     */
    fun mvcContentNegotiationManager(): ContentNegotiationManager {
        return ContentNegotiationManager(
            HeaderContentNegotiationStrategy(),
            FixedContentNegotiationStrategy(defaultContentType)
        )
    }

    /**
     * Defines content negotiation manager with the fixed content strategy of
     * of serving content of media type application json in case of exception scenarios.
     *
     * @return the configured content negotiation manager
     */
    fun exceptionContentNegotiationManager(): ContentNegotiationManager {
        return ContentNegotiationManager(FixedContentNegotiationStrategy(defaultContentType))
    }

    /**
     * Redefines all the response based exception handlers to use json converter, handle method return resolver
     * to use http entity processor with content negotiation strategy of
     * to serve fixed content of media type application json and a
     * default spring exception resolver in case of framework errors.
     *
     * @return the configured handler exception resolver
     */
    override fun configureHandlerExceptionResolvers(
        exceptionResolvers: MutableList<HandlerExceptionResolver>
    ) {
        val exceptionHandlerExceptionResolver = ExceptionHandlerExceptionResolver()
        val messageConverters: MutableList<HttpMessageConverter<*>> = ArrayList()
        messageConverters.add(mappingJackson2HttpMessageConverter())
        exceptionHandlerExceptionResolver.contentNegotiationManager = exceptionContentNegotiationManager()
        exceptionHandlerExceptionResolver.messageConverters = messageConverters
        exceptionHandlerExceptionResolver.applicationContext = applicationContext
        val returnValueHandlers: MutableList<HandlerMethodReturnValueHandler> = ArrayList()
        returnValueHandlers
            .addAll(
                listOf(
                    HttpEntityMethodProcessor(messageConverters, exceptionContentNegotiationManager()),
                    RequestResponseBodyMethodProcessor(messageConverters, exceptionContentNegotiationManager())
                )
            )
        exceptionHandlerExceptionResolver.setReturnValueHandlers(returnValueHandlers)
        exceptionHandlerExceptionResolver.afterPropertiesSet()
        exceptionResolvers.add(exceptionHandlerExceptionResolver)
        val responseStatusExceptionResolver = ResponseStatusExceptionResolver()
        responseStatusExceptionResolver.setMessageSource(applicationContext)
        exceptionResolvers.add(responseStatusExceptionResolver)
        exceptionResolvers.add(DefaultHandlerExceptionResolver())
    }
}
