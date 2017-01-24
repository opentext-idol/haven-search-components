/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.exceptions;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.idol.annotations.IdolService;
import com.hp.autonomy.searchcomponents.idol.exceptions.codes.AnswerServerErrorCode;
import com.hp.autonomy.searchcomponents.idol.exceptions.codes.IdolErrorCodes;
import com.hp.autonomy.types.idol.marshalling.marshallers.AciErrorExceptionBuilder;
import com.hp.autonomy.types.idol.responses.Error;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import static com.hp.autonomy.searchcomponents.idol.exceptions.AciErrorExceptionAspectTest.ACI_ERROR_EXCEPTION_ASPECT_TEST_PROPERTY;
import static org.mockito.Mockito.mock;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AciErrorExceptionAspectTest.AciErrorExceptionAspectConfiguration.class, properties = ACI_ERROR_EXCEPTION_ASPECT_TEST_PROPERTY, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AciErrorExceptionAspectTest {
    static final String ACI_ERROR_EXCEPTION_ASPECT_TEST_PROPERTY = "aciErrorExceptionAspectTest";

    @Autowired
    private TestService testService;

    @Test(expected = EnumeratedAciErrorException.class)
    public void recognisedError() {
        testService.throwRecognisedException();
    }

    @Test(expected = AciErrorException.class)
    public void unrecognisedError() {
        testService.throwUnrecognisedException();
    }

    @Test(expected = EnumeratedAciServiceException.class)
    public void serviceError() {
        testService.throwServiceException();
    }

    @ComponentScan
    @Configuration
    @EnableAspectJAutoProxy
    @ConditionalOnProperty(ACI_ERROR_EXCEPTION_ASPECT_TEST_PROPERTY)
    public static class AciErrorExceptionAspectConfiguration {
    }

    public interface TestService {
        void throwRecognisedException();

        void throwUnrecognisedException();

        void throwServiceException();
    }

    @Service
    @IdolService(IdolErrorCodes.ANSWER_SERVER)
    @ConditionalOnProperty(ACI_ERROR_EXCEPTION_ASPECT_TEST_PROPERTY)
    public static class TestServiceImpl implements TestService {
        @Override
        public void throwRecognisedException() {
            final Error error = new Error();
            error.setErrorid(AnswerServerErrorCode.STOP_WORDS.getErrorId());
            throw new AciErrorExceptionBuilder(error).build();
        }

        @Override
        public void throwUnrecognisedException() {
            throw new AciErrorExceptionBuilder(new Error()).build();
        }

        @Override
        public void throwServiceException() {
            throw new EnumeratedAciServiceException("SomeMessage", mock(AciServiceError.class));
        }
    }
}
