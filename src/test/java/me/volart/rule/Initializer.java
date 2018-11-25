package me.volart.rule;

import spark.Service;

@FunctionalInterface
public interface Initializer {

  void init(Service service);

}