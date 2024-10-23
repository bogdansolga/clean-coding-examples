package com.great.project.patientadmin.core.domain.model;

import java.io.Serializable;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Age implements Serializable {
    private transient @Setter Optional<Integer> value;
    private @Setter String unit;
    private String displayLongText;
    private String displayShortText;

    public void setDisplayLongText(String unitLong) {
        this.displayLongText = ((value.isPresent()) ? value.get().toString() : "") + " " + unitLong;
    }

    public void setDisplayShortText(String unitShort) {
        this.displayShortText = ((value.isPresent()) ? value.get().toString() : "") + " " + unitShort;
    }

    @Override
    public String toString() {
        return displayLongText;
    }

}