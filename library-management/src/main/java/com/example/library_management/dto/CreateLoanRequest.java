// CreateLoanRequest.java
package com.example.library_management.dto;

public class CreateLoanRequest {
    private Long memberId;
    private String isbn;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}