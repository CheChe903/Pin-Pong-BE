package com.example.pin_pong.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubCommitDetail {
    private String patch;

    @JsonProperty("files")
    private List<FileDetail> files;

    public String getPatch() {
        if (files != null && !files.isEmpty()) {
            return files.get(0).getPatch();  // assuming we want the patch of the first file
        }
        return null;
    }

    public void setPatch(String patch) {
        this.patch = patch;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FileDetail {
        private String patch;

        public String getPatch() {
            return patch;
        }

        public void setPatch(String patch) {
            this.patch = patch;
        }
    }
}
