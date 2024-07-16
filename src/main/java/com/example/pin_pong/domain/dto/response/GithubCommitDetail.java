package com.example.pin_pong.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubCommitDetail {
    @JsonProperty("files")
    private List<FileDetail> files;

    public String getPatch() {
        if (files != null && !files.isEmpty()) {
            return files.stream()
                    .map(FileDetail::getPatch)
                    .filter(patch -> patch != null && !patch.isEmpty())
                    .collect(Collectors.joining("\n\n"));
        }
        return null;
    }

    public void setFiles(List<FileDetail> files) {
        this.files = files;
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
