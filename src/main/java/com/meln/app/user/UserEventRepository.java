package com.meln.app.user;

import com.meln.app.user.model.UserEvent;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserEventRepository implements PanacheRepository<UserEvent> {

}
