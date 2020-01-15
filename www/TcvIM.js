var exec = require('cordova/exec');

var PLUGIN_NAME = 'FirstToast';

var FirstToast = {

    toast: function (args, cb) {
        exec(cb, null, PLUGIN_NAME, "toast", [args]);
    },

    initNIMClient: function (param, successCb, errorCb) {
        exec(successCb, errorCb, PLUGIN_NAME, 'initNIMClient', [param]);
    },

    registerObserver: function (successCb, errorCb) {
        exec(successCb, errorCb, PLUGIN_NAME, 'registerObserver', []);
    },

    logoutNIMClient: function (param, cb) {
        exec(cb, null, PLUGIN_NAME, 'logoutNIMClient', [param]);
    }
};

module.exports = FirstToast;