package org.bremersee.apiclient.webflux.contract.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestBody;

class AbstractRequestBodyInserterTest {

  @Test
  void canInsert() throws Exception {
    AbstractRequestBodyInserter target = mock(AbstractRequestBodyInserter.class);
    when(target.canInsert(any(Invocation.class)))
        .thenCallRealMethod();
    when(target.canInsert(anyList()))
        .thenCallRealMethod();
    when(target.findPossibleBodies(any(Invocation.class)))
        .thenCallRealMethod();
    when(target.isPossibleBody(any(InvocationParameter.class)))
        .thenCallRealMethod();
    when(target.hasMappingAnnotation(any(InvocationParameter.class)))
        .thenCallRealMethod();
    when(target.isPossibleBodyValue(any(InvocationParameter.class)))
        .thenReturn(true);
    Method method = Example.class.getMethod("methodA", String.class);
    Invocation invocation = new Invocation(Example.class, method, new Object[]{"text"});
    assertThat(target.canInsert(invocation))
        .isTrue();
  }

  interface Example {

    void methodA(@RequestBody String body);
  }
}