import angular from 'angular';
import uiRouter from 'angular-ui-router';
import setComponent from './set.component.js';
import uicommons from 'openmrs-contrib-uicommons';

let setModule = angular.module('set', [ uiRouter, 'openmrs-contrib-uicommons'])
    .config(($stateProvider, $urlRouterProvider) => {
        "ngInject";
        $urlRouterProvider.otherwise('/');

        $stateProvider.state('set', {
            url: '/set/:UUID',
            template: "<set mapping='$resolve.mapping'></set>",
            resolve: {
                mapping: mapping
            }
        })
    })
    .component('set', setComponent);

function mapping(openmrsRest, $stateParams) {

    return openmrsRest.getFull('metadatamapping/termmapping',
        {uuid: $stateParams.UUID}).then(function(response){
            return response;
        })
}

export default setModule;