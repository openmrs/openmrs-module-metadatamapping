class CreateSourceController {
  constructor($stateParams, $rootScope, $location, openmrsNotification, openmrsRest) {
    "ngInject"
    $rootScope.links = {};
    $rootScope.links["Manage Mappings"] = "/";
    $rootScope.links['{{ "createsource.title" | translate }}'] = "source/add";

    var vm = this;
    vm.source = {name : "", description : ""};
    vm.isCorrect = isCorrect;
    vm.saveSource = saveSource;

    openmrsNotification.routeNotification();

    function isCorrect(){
      return vm.source.name !== undefined && vm.source.name !== "";
    }

    function saveSource(){
      openmrsRest.create("metadatamapping/source", vm.source).then(handleSuccess, handleException)
    }

    function handleException(exception){
      openmrsNotification.error(exception.data.error.message);
    }

    function handleSuccess(success){
      $location.path('/').search({successToast: vm.source.name+" has been saved"});
    }
  }
}

export default CreateSourceController;
