'use strict';

var opennms = require('../../util/opennms')(casper),
	utils = require('utils');

var integerFields = {
	timeout: 'timeout',
	retryCount: 'Retry Count',
	port: 'Port',
	maxVarsPerPdu: 'Max Vars Per Pdu',
	maxRepetitions: 'Max Repetitions',
	maxRequestSize: 'Max Request Size'
};

var badInputs = ['abc', '-5', '0'];

var checkInput = function(fieldName, input) {
	// first put in an IP address or it won't do other form validation
	casper.then(function() {
		var formInput = {
			firstIPAddress: '1.2.3.4'
		};
		formInput[fieldName] = input;
		casper.fill('form[name="snmpConfigForm"]', formInput, false);
	});
	casper.then(function() {
		casper.waitForAlert(function(response) {
			if (fieldName === 'maxRequestSize') {
				// special case
				casper.test.assertEquals(response.data, input + ' is not a valid ' + integerFields[fieldName] + '. Please enter a number greater or equal than 484 or leave it empty.', 'Expect an error when inserting ' + input + ' into ' + fieldName);
			} else {
				casper.test.assertEquals(response.data, input + ' is not a valid ' + integerFields[fieldName] + '. Please enter a number greater than 0 or leave it empty.', 'Expect an error when inserting ' + input + ' into ' + fieldName);
			}
			return true;
		}, function() {
			casper.test.fail('Error never recieved for form field: ' + fieldName + ' with input: ' + input);
		});
		casper.clickLabel('Save Config');
	});
};

var checkIntegerField = function(fieldName) {
	casper.thenOpen(opennms.root() + '/admin/snmpConfig');

	for (var i=0; i < badInputs.length; i++) {
		var badInput = badInputs[i];

		checkInput(fieldName, badInput);
	}

	casper.then(function() {
		var formInput = {
			firstIPAddress: '1.2.3.4'
		};
		formInput[fieldName] = '1000';
		casper.fill('form[name="snmpConfigForm"]', formInput, false);
	});
	casper.then(function() {
		casper.clickLabel('Save Config');
	});
	casper.then(function() {
		casper.test.assertSelectorHasText('div#content > h3', 'Finished configuring SNMP. OpenNMS does not need to be restarted.');
	});
};

var enterIp = function(first, last, alertPass, alertTimeout) {
	casper.thenOpen(opennms.root() + '/admin/snmpConfig');
	first = first || '';
	last  = last || '';

	casper.then(function() {
		casper.fill('form[name="snmpConfigForm"]', {
			firstIPAddress: first,
			lastIPAddress: last,
			timeout: '',
			retryCount: '',
			port: '',
			maxVarsPerPdu: '',
			maxRepetitions: '',
			maxRequestSize: ''
		}, false);
	});
	casper.then(function() {
		casper.clickLabel('Save Config');
	});
	if (alertPass) {
		casper.waitForAlert(alertPass, alertTimeout);
	}
};

var testInvalidIp = function(first, last, error) {
	casper.thenOpen(opennms.root() + '/admin/snmpConfig');
	casper.then(function() {
		var alertFuncPass = function(response) {
			casper.test.assertEquals(response.data, error, 'Expect an error with firstIPAddress=' + first + ', lastIPAddress=' + last);
			return true;
		};
		var alertFuncTimeout = function() {
			casper.test.fail('Error never recieved: ' + error);
		};
		enterIp(first, last, alertFuncPass, alertFuncTimeout);
	});
};

casper.test.begin('Configure SNMP Community Names by IP Address > Integer Validation', 28, {
	setUp: function() {
		opennms.initialize();
		opennms.login();
	},

	test: function(test) {
		var keys = Object.keys(integerFields);
		for (var i=0; i < keys.length; i++) {
			checkIntegerField(keys[i]);
		}
		casper.wait(500);

		testInvalidIp('1234', '', '1234 is not a valid IP address!');
		testInvalidIp('1.1.1.1', 'abc', 'abc is not a valid IP address!');

		enterIp('1.1.1.1', '');
		casper.then(function() {
			casper.test.assertSelectorHasText('div#content > h3', 'Finished configuring SNMP. OpenNMS does not need to be restarted.');
		});

		enterIp('1.1.1.1', '1.1.1.2');
		casper.then(function() {
			casper.test.assertSelectorHasText('div#content > h3', 'Finished configuring SNMP. OpenNMS does not need to be restarted.');
		});

		opennms.finished(test);
	}
});

