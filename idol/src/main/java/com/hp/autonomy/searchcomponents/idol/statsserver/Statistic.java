/*
 * Copyright 2015-2016 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
