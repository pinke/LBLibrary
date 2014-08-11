cordova.define("com.livebos.cordova.library.LiveBOSLibrary", function (require, exports, module) {

    var exec = require('cordova/exec');
    var channel = require('cordova/channel');
    var modulemapper = require('cordova/modulemapper');
    var urlutil = require('cordova/urlutil');

    function LiveBOSLibrary() {
        this.channels = {
            'loadstart': channel.create('loadstart'),
            'loadstop': channel.create('loadstop'),
            'loaderror': channel.create('loaderror'),
            'exit': channel.create('exit')
        };
    }

    LiveBOSLibrary.prototype = {
        _eventHandler: function (event) {
            if (event.type in this.channels) {
                this.channels[event.type].fire(event);
            }
        },
        close: function (eventname) {
            exec(null, null, "LiveBOSLibrary", "close", []);
        },
        addEventListener: function (eventname, f) {
            if (eventname in this.channels) {
                this.channels[eventname].subscribe(f);
            }
        },
        removeEventListener: function (eventname, f) {
            if (eventname in this.channels) {
                this.channels[eventname].unsubscribe(f);
            }
        }
    };

    module.exports = function (strUrl, strWindowName, strWindowFeatures) {
        // Don't catch calls that write to existing frames (e.g. named iframes).
        if (window.frames && window.frames[strWindowName]) {
            var origOpenFunc = modulemapper.getOriginalSymbol(window, 'launch');
            return origOpenFunc.apply(window, arguments);
        }

        strUrl = urlutil.makeAbsolute(strUrl);
        var iab = new LiveBOSLibrary();
        var cb = function (eventname) {
            iab._eventHandler(eventname);
        };

        strWindowFeatures = strWindowFeatures || "";

        exec(cb, cb, "LiveBOSLibrary", "launch", [strUrl, strWindowName, strWindowFeatures]);
        return iab;
    };


});
