package com.gavin.app.webflux.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;
    @NotBlank
    @Size(min = 4, max = 50)
    private String login;
    @NotBlank
    @Size(min = 11, max = 11)
    private String phone;
    @Email
    private String email;
    private String createdBy;
    private Date createdAt;
    private String updatedBy;
    private Date updatedAt;

}
