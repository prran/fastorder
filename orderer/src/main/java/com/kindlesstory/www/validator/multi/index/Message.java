package com.kindlesstory.www.validator.multi.index;

public enum Message
{
    FORMAT_REJECT {
        @Override
        public String toString() {
            return "FORMAT_REJECT";
        }
    }, 
    TYPE_REJECT {
        @Override
        public String toString() {
            return "TYPE_REJECT";
        }
    }, 
    XSS_REJECT {
        @Override
        public String toString() {
            return "XSS_REJECT";
        }
    },
    UNPROCESSABLE_CODE_REJECT {
        @Override
        public String toString() {
            return "UNPROCESSABLE_CODE_REJECT";
        }
    },
}