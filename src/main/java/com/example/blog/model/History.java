package com.example.blog.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "HISTORY")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long rank;

    @Column(nullable = false)
    private String query;
    
    @Column(nullable = true)
    private int cnt;

    @Builder
    public History(String query, int cnt) {
        this.query = query;
        this.cnt = cnt;
    }
}
