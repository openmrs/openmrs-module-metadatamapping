import angular from 'angular';
import uiRouter from 'angular-ui-router';
import mainComponent from './main.component.js';
import Home from '../home/home';
import Set from '../set/set';
import Mapping from '../mapping/mapping';
import uiBootstrap from 'angular-ui-bootstrap';
import CreateSource from '../createsource/createsource'
import uicommons from 'openmrs-contrib-uicommons';

let MetadataMappingOwa = angular.module('Metadata Mapping', [ uiRouter, Home.name, Set.name, Mapping.name, CreateSource.name, 'openmrs-contrib-uicommons', uiBootstrap
    ])
    .component('main', mainComponent);

export default MetadataMappingOwa;
