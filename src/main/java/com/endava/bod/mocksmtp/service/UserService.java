package com.endava.bod.mocksmtp.service;

import com.endava.bod.mocksmtp.domain.Activation;
import com.endava.bod.mocksmtp.domain.User;
import io.vavr.control.Try;

public interface UserService {

    Try<Boolean> create(User user);
    Try<Boolean> activate(Activation activation);
}
