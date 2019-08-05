package com.cliqz.nove.example;

class Messages {
    static class RequestNextValue {}
    static class NextValueResponse {
        final int value;

        NextValueResponse(int value) {
            this.value = value;
        }
    }
}
