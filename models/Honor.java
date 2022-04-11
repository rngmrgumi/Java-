package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "honor",
        uniqueConstraints = {
        })
public class Honor {
    @Id
    private Integer id;

    @NotBlank
    @Size
    private String num;

    @Size
    private String name;

    @Size
    private String honor;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getHonor() { return honor; }

    public void setHonor(String honor) { this.honor = honor; }

    public String getNum() { return num; }

    public void setNum(String num) { this.num = num; }
}

