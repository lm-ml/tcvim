
var exec = require('cordova/exec');

var PLUGIN_NAME = 'FirstToast';

exports.toast = function (arg0, success, error) {

    exec(success, error, PLUGIN_NAME, "toast", [arg0]);

    exec(success, error, PLUGIN_NAME, "initNIMClient", [arg0]);

    exec(success, error, PLUGIN_NAME, "logoutNIMClient", [arg0]);
};
