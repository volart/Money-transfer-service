package me.volart.rule;

import org.junit.rules.ExternalResource;
import spark.Service;


public class SparkRule extends ExternalResource {


  private Initializer serviceInitializer;
  private Service service;

  public SparkRule(Initializer svcInit) {
    this.serviceInitializer = svcInit;
  }

  @Override
  protected void before() {
    service = Service.ignite();
    serviceInitializer.init(service);
    service.awaitInitialization();
  }

  @Override
  protected void after() {
    service.stop();
  }

}
