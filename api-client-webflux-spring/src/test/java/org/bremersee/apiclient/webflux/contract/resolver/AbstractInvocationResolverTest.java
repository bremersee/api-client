package org.bremersee.apiclient.webflux.contract.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.contract.Invocation;
import org.bremersee.apiclient.webflux.contract.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.Answer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(SoftAssertionsExtension.class)
class AbstractInvocationResolverTest {

  @Test
  void findResolver(SoftAssertions softly) {
    //noinspection unchecked
    InvocationParameterResolver<Object, MultiValueMap<String, Object>> resolver = mock(
        InvocationParameterResolver.class);
    when(resolver.canResolve(any())).thenReturn(true);
    AbstractInvocationResolver<Object, MultiValueMap<String, Object>> target = new ExampleInvocationResolver(
        List.of(resolver));
    InvocationParameter invocationParameter = mock(InvocationParameter.class);
    softly.assertThat(target.findResolver(invocationParameter))
        .isPresent();

    //noinspection unchecked
    reset(resolver);
    when(resolver.canResolve(any())).thenReturn(false);
    softly.assertThat(target.findResolver(invocationParameter))
        .isEmpty();
  }

  @Test
  void apply() {
    //noinspection unchecked
    InvocationParameterResolver<Object, MultiValueMap<String, Object>> resolver = mock(
        InvocationParameterResolver.class);
    when(resolver.canResolve(any())).thenReturn(true);

    Invocation invocation = mock(Invocation.class);
    InvocationParameter param0 = mock(InvocationParameter.class);
    MultiValueMap<String, Object> map0 = new LinkedMultiValueMap<>();
    map0.addAll("a", List.of("1"));
    when(resolver.resolve(eq(param0))).thenReturn(map0);

    InvocationParameter param1 = mock(InvocationParameter.class);
    MultiValueMap<String, Object> map1 = new LinkedMultiValueMap<>();
    map0.addAll("a", List.of("2"));
    when(resolver.resolve(eq(param1))).thenReturn(map1);

    InvocationParameter param2 = mock(InvocationParameter.class);
    MultiValueMap<String, Object> map2 = new LinkedMultiValueMap<>();
    map0.addAll("c", List.of("3"));
    when(resolver.resolve(eq(param2))).thenReturn(map2);

    when(invocation.toMethodParameterStream())
        .thenAnswer((Answer<Stream<InvocationParameter>>) invocationOnMock -> Stream.of(param0, param1, param2));
    MultiValueMap<String, Object> expected = new LinkedMultiValueMap<>();
    expected.addAll(map0);
    expected.addAll(map1);
    expected.addAll(map2);

    AbstractInvocationResolver<Object, MultiValueMap<String, Object>> target = new ExampleInvocationResolver(
        List.of(resolver));
    MultiValueMap<String, Object> actual = target.apply(invocation);

    assertThat(actual)
        .isEqualTo(expected);
  }

  static class ExampleInvocationResolver
      extends AbstractInvocationResolver<Object, MultiValueMap<String, Object>> {

    public ExampleInvocationResolver(
        Collection<? extends InvocationParameterResolver<Object, MultiValueMap<String, Object>>> resolvers) {
      super(resolvers);
    }

    @Override
    public MultiValueMap<String, Object> get() {
      return new LinkedMultiValueMap<>();
    }
  }
}