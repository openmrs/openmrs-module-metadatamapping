import angular from 'angular';
import uiRouter from 'angular-ui-router';
import createSourceComponent from './createsource.component.js';
import uicommons from 'openmrs-contrib-uicommons';

let CreateSourceModule = angular.module('createsource', [ uiRouter, 'openmrs-contrib-uicommons'])
    .config(($stateProvider, $urlRouterProvider) => {
        "ngInject";
        $urlRouterProvider.otherwise('/');

        $stateProvider.state('createsource', {
            url: '/source/add',
            template: "<createsource></createsource>"
        })
    })
    .component('createsource', createSourceComponent);

export default CreateSourceModule;
