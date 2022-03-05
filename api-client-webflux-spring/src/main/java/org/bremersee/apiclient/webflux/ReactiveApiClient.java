package org.bremersee.apiclient.webflux;

import static java.util.Objects.isNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.bremersee.apiclient.ApiClient;
import org.bremersee.apiclient.webflux.contract.FunctionBundle;
import org.bremersee.apiclient.webflux.contract.ReactiveInvocationHandler;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

public class ReactiveApiClient extends ApiClient {

  private final WebClient.Builder webClientBuilder;

  public ReactiveApiClient(WebClient.Builder webClientBuilder) {
    this.webClientBuilder = isNull(webClientBuilder) ? WebClient.builder() : webClientBuilder;
  }

  @Override
  public <T> T newInstance(Class<T> target, String baseUrl) {
    return new Builder()
        .webClient(webClientBuilder.baseUrl(baseUrl).build())
        .build(target);
  }

  /**
   * Default api client builder.
   *
   * @return the web client proxy builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * The builder.
   */
  public static class Builder {

    private WebClient webClient;

    private FunctionBundle functionBundle;

    private ErrorFunctionBundle errorFunctionBundle;

    /**
     * Sets the web client with the base url.
     *
     * @param webClient the web client
     * @return the builder
     */
    public Builder webClient(WebClient webClient) {
      this.webClient = webClient;
      return this;
    }

    public Builder functionBundle(FunctionBundle functionBundle) {
      this.functionBundle = functionBundle;
      return this;
    }

    public Builder errorFunctionBundle(ErrorFunctionBundle errorFunctionBundle) {
      this.errorFunctionBundle = errorFunctionBundle;
      return this;
    }

    /**
     * Builds proxy.
     *
     * @param <T> the type parameter
     * @param target the target
     * @return the t
     */
    public <T> T build(Class<T> target) {
      Assert.notNull(target, "Target must be present.");
      InvocationHandler handler = new ReactiveInvocationHandler(
          target,
          webClient,
          functionBundle,
          errorFunctionBundle);
      //noinspection unchecked
      return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, handler);
    }

  }

}
