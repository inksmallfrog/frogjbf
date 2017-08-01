package com.inksmallfrog.frogjbf.test.entity;

import com.inksmallfrog.frogjbf.annotation.Column;
import com.inksmallfrog.frogjbf.annotation.TableName;

/**
 * Created by inksmallfrog on 17-8-1.
 */
@TableName(name="USER")
public class UserBean {
    @Column(
            name="ID"

    )
    private long id;
    @Column(name="EMAIL")
    private String email;
    @Column(name="PASSWORD")
    private String pwd;
    @Column(name="AVATOR")
    private Object avator;
}
