/*
 * Copyright 2015-2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.statsserver;

import com.autonomy.aci.client.annotations.IdolDocument;
import com.autonomy.aci.client.annotations.IdolField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@IdolDocument("stat")
public class Statistic {
    private String name;
    private String idol;

    private Long period;
    private Boolean dynamic;
    private String type;

    @IdolField("name")
    public void setName(final String name) {
        this.name = name;
    }

    @IdolField("period")
    public void setPeriod(final long period) {
        this.period = period;
    }

    @IdolField("type")
    public void setType(final String type) {
        this.type = type.toLowerCase();
    }

    @IdolField("dynamic")
    public void setDynamic(final boolean dynamic) {
        this.dynamic = dynamic;
    }
}
