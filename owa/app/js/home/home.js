import angular from 'angular';
import uiRouter from 'angular-ui-router';
import homeComponent from './home.component.js';
import uicommons from 'openmrs-contrib-uicommons';

let homeModule = angular.module('home', [ uiRouter, 'openmrs-contrib-uicommons'])
    .config(($stateProvider, $urlRouterProvider) => {
        "ngInject";
        $urlRouterProvider.otherwise('/');

        $stateProvider.state('home', {
            url: '/?infoToast&errorToast&successToast&warningToast',
            template: "<home></home>"
        })
    })
    .component('home', homeComponent);

export default homeModule;
