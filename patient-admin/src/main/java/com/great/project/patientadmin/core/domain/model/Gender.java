package com.great.project.patientadmin.core.domain.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Gender implements Serializable {
    private String displayLongText;
    private String displayShortText;

    @Override
    public String toString() {
        return displayLongText + " " + displayShortText;
    }

    public void setDisplayLongText(String displayLongText) {
        this.displayLongText = StringUtils.capitalize(displayLongText.toLowerCase());
    }

    public void setDisplayShortText(String displayShortText) {
        if (!this.displayLongText.equalsIgnoreCase("")) {
            this.displayShortText = StringUtils.capitalize(displayShortText.toLowerCase());
        } else {
            this.displayShortText = "";
        }
    }
}
