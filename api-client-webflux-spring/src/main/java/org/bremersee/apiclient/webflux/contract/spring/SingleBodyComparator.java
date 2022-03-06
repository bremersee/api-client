package org.bremersee.apiclient.webflux.contract.spring;

import java.util.Comparator;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.web.bind.annotation.RequestBody;

public class SingleBodyComparator implements Comparator<InvocationParameter> {

  @Override
  public int compare(InvocationParameter o1, InvocationParameter o2) {
    boolean a1 = o1.hasParameterAnnotation(RequestBody.class);
    boolean a2 = o2.hasParameterAnnotation(RequestBody.class);
    if (a1 && a2) {
      return 0;
    }
    if (a1) {
      return -1;
    }
    if (a2) {
      return 1;
    }
    return Integer.compare(o1.getIndex(), o2.getIndex());
  }
}
