package org.elkendev.exception;

public class ItemNotFoundException extends Exception {

    private Long id;

    public ItemNotFoundException(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
