package pl.beone.operaton.operaton_history.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    public static final long serialVersionUID = 1L;

    private String username;
    private String fullName;
}

