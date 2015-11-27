package com.hp.autonomy.frontend.view.idol;

import com.autonomy.aci.actions.idol.query.QueryResponseProcessor;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.view.idol.configuration.ViewCapable;
import com.hp.autonomy.frontend.view.idol.configuration.ViewConfig;
import com.hp.autonomy.test.xml.XmlTestUtils;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IdolViewServerServiceTest {
    @Mock
    private AciService contentAciService;

    @Mock
    private AciService viewAciService;

    @Mock
    private ConfigService<? extends ViewCapable> configService;

    @Mock
    private ViewCapable viewCapableConfig;

    private IdolViewServerService idolViewServerService;

    @Before
    public void setUp() {
        final ViewConfig viewConfig = new ViewConfig.Builder().setReferenceField("URL").build();
        when(viewCapableConfig.getViewConfig()).thenReturn(viewConfig);
        when(configService.getConfig()).thenReturn(viewCapableConfig);

        idolViewServerService = new IdolViewServerService(contentAciService, viewAciService, configService);
    }

    @Test
    public void testViewDocument() throws XMLStreamException, ViewDocumentNotFoundException, ViewNoReferenceFieldException, ReferenceFieldBlankException {
        final XMLStreamReader getContentXmlStreamReader = XmlTestUtils.getResourceAsXMLStreamReader("/get-content.xml");

        when(contentAciService.executeAction(any(AciParameters.class), any(QueryResponseProcessor.class))).thenReturn(new QueryResponseProcessor().process(getContentXmlStreamReader));

        idolViewServerService.viewDocument("dede952d-8a4d-4f54-ac1f-5187bf10a744", mock(Processor.class));

        verify(viewAciService, times(1)).executeAction(argThat(AciParameterMatcher.aciParametersWith("Reference", "http://en.wikipedia.org/wiki/Car")), any(Processor.class));
    }

    private static class AciParameterMatcher extends ArgumentMatcher<AciParameters> {

        private final String parameter;
        private final String value;

        private AciParameterMatcher(final String parameter, final String value) {
            this.parameter = parameter;
            this.value = value;
        }

        private static Matcher<AciParameters> aciParametersWith(final String parameter, final String value) {
            return new AciParameterMatcher(parameter, value);
        }

        @Override
        public boolean matches(final Object argument) {
            if (!(argument instanceof AciParameters)) {
                return false;
            }

            final AciParameters parameters = (AciParameters) argument;

            return value.equals(parameters.get(parameter));
        }
    }
}