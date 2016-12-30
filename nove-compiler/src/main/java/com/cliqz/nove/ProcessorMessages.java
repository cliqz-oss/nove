package com.cliqz.nove;

import javax.lang.model.element.Modifier;

/**
 * Created by stefano on 30/12/16.
 */
final class ProcessorMessages {

    private ProcessorMessages() {}

    static final String ERROR_INVALID_MODIFIER_PRIVATE = "Invalid modifier: private";
    static final String ERROR_INVALID_MODIFIER_PROTECTED = "Invalid modifier: protected";
    static final String ERROR_INVALID_MODIFIER_ABSTRACT = "Invalid modifier: abstract";
    static final String ERROR_TOO_MANY_ARGUMENTS = "Subscriber must have a single parameter";
    static final String ERROR_PRIMITIVE_PARAMETERS = "Subscriber can't use primitives as parameters";

    static final String WARNING_NON_VOID_RESULT = "Subscriber should return void";

    static String getInvalidModifierMessage(Modifier modifier) {
        switch (modifier) {
            case ABSTRACT:
                return ERROR_INVALID_MODIFIER_ABSTRACT;
            case PRIVATE:
                return ERROR_INVALID_MODIFIER_PRIVATE;
            case PROTECTED:
                return ERROR_INVALID_MODIFIER_PROTECTED;
            default:
                return "";
        }
    }
}
