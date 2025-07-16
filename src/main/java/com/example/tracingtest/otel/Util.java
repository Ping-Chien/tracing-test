package com.example.tracingtest.otel;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.context.Context;

public class Util {
    
    public static final AttributeKey<String> WIDGET_COLOR = AttributeKey.stringKey("widget.color");
    public static final AttributeKey<String> WIDGET_SHAPE = AttributeKey.stringKey("widget.shape");
    public static final Attributes WIDGET_RED_CIRCLE = Attributes.of(WIDGET_COLOR, "red_circle");

    public static String computeWidgetShape() {
        return "computed_shape";
    }
    public static Context customContext() {
        return Context.current();
    }
    public static String computeWidgetColor() {
        return "computed_color";
    }}
