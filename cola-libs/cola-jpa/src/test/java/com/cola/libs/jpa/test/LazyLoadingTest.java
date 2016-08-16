/*
 * Copyright 2002-${Year} the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cola.libs.jpa.test;

import com.cola.libs.jpa.entities.Language;
import com.cola.libs.jpa.entities.Role;
import com.cola.libs.jpa.entities.Rolelp;
import com.cola.libs.jpa.services.FlexibleSearchService;
import com.cola.libs.jpa.services.ModelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * cola
 * Created by jiachen.shi on 8/3/2016.
 */
public class LazyLoadingTest {

    @Autowired
    private ModelService modelService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Role init(){

        UUID uuid = UUID.randomUUID();
        Language language = new Language();
        language.setIsoCode(uuid.toString().substring(0, 20));
        language.setCreateBy(1L);
        language.setLastModifiedBy(1L);
        language = modelService.save(language);

        uuid = UUID.randomUUID();
        Role role = new Role();
        role.setCode(uuid.toString().substring(0, 20));
        role.setCreateBy(1L);
        role.setLastModifiedBy(1L);
        role = modelService.save(role);

        Rolelp rolelp = new Rolelp();
        rolelp.setCreateBy(1L);
        rolelp.setLanguage(language);
        rolelp.setRole(role);
        rolelp.setName("ABC");
        rolelp.setLastModifiedBy(1L);
        rolelp = modelService.save(rolelp);

        return role;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Role test(Long roleId){
        Role role = modelService.load(Role.class, roleId);
        Collection<Rolelp> rolelps = role.getRolelps();

        Role role1 = modelService.get(Role.class, roleId);
        Collection<Rolelp> rolelps1 = role1.getRolelps();
        return role;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void destroy(Role role){
        Map<String, Object> condition = new HashMap<>();
        condition.put("role", role);
        Rolelp rolelp = flexibleSearchService.uniqueQuery(Rolelp.class, condition, null);
        if(rolelp != null){
            Language language = rolelp.getLanguage();
            modelService.delete(rolelp);
            if(language !=  null){
                modelService.delete(language);
            }
        }
        modelService.delete(role);
    }

}