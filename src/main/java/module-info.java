module stitch {
    requires java.compiler;
    requires java.net.http;
    requires java.xml;

    exports stitch;
    exports stitch.domain;
    exports stitch.resolve;
    exports stitch.util;
    exports stitch.engine;
}

