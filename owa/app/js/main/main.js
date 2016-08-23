import angular from 'angular';
import uiRouter from 'angular-ui-router';
import mainComponent from './main.component.js';
import Home from '../home/home';
import Set from '../set/set';
import uicommons from 'openmrs-contrib-uicommons';

let MetadataMappingOwa = angular.module('Metadata Mapping', [ uiRouter, Home.name, Set.name, 'openmrs-contrib-uicommons'
    ])
    .component('main', mainComponent);

export default MetadataMappingOwa;
