import angular from 'angular';
import uiRouter from 'angular-ui-router';
import mappingComponent from './mapping.component.js';
import uicommons from 'openmrs-contrib-uicommons';
import uiBootstrap from 'angular-ui-bootstrap';

let mappingModule = angular.module('mapping', [ uiRouter, 'openmrs-contrib-uicommons', uiBootstrap])
    .config(($stateProvider, $urlRouterProvider) => {
        "ngInject";
        $urlRouterProvider.otherwise('/');

        $stateProvider.state('mapping', {
            url: '/mapping',
            template: "<mapping metadata-sources='$resolve.metadataSources'></mapping>",
            resolve: {
                metadataSources: metadataSources
            }
        })
    })
    .component('mapping', mappingComponent);

function metadataSources(openmrsRest) {
    return openmrsRest.listFull('metadatamapping/source').then(function(response){
        return response.results;
    })
}

export default mappingModule;
