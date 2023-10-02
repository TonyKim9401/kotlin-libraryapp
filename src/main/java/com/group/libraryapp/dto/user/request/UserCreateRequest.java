package com.group.libraryapp.dto.user.request;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserCreateRequest {

  private String name;
  private Integer age;

  public String getName() {
    return name;
  }

  public Integer getAge() {
    return age;
  }

  public UserCreateRequest(String name, Integer age) {
    this.name = name;
    this.age = age;
  }
}
