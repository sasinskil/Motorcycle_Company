<template>
  <div id="editMotorcycle" class="main-container--edit">
     <h1 class="main-container--edit__title">Modyfikuj model z katalogu: {{motorcycle.model}}</h1>
     
    <button class="single-motorcycle__button" @click="back"><font-awesome-icon class="plus-icon icon" icon="arrow-left" />Powrót</button>
    <form class="form">
      <h2 class="form__title--main">Dane szczegółowe</h2>
      <p class="form__wrapper">
        <label class="form__label" for="model">Model:</label>
        <input class="form__input date-input" type="text" name="model" id="model" required v-model.lazy="motorcycle.model" />
      </p>
      <p class="form__wrapper">
        <label class="form__label" for="brand">Marka:</label>
        <input class="form__input" type="text" name="brand" id="brand" required v-model.lazy="motorcycle.brand" />
      </p>
      <p class="form__wrapper">
        <label class="form__label" for="classif">Klasyfikacja:</label> 
        <select class="form__input" name="classif" id="classif" required v-model.lazy="motorcycle.classification">
          <option v-for="classification in classifications" :key="classification">{{classification}}</option>
        </select>
      </p>
      <p class="form__wrapper">
        <label class="form__label" for="power">Moc (km):</label>
        <input class="form__input" type="number" name="power" id="power" required v-model.lazy="motorcycle.power" />
      </p>
      <p class="form__wrapper">
        <label class="form__label" for="capacity">Pojemność silnika (cm <span class="cubic-centimeters--edit">3</span> ):</label>
        <input class="form__input" type="number" placeholder="0.00" step="0.01" name="capacity" id="capacity" required v-model.lazy="motorcycle.capacity" />
      </p>
      <p class="form__wrapper">
        <label class="form__label" for="engType">Rodzaj silnika:</label>
        <input class="form__input" type="text" name="engType" id="engType" required v-model.lazy="motorcycle.engineType" />
      </p>
      <p>
        <label class="form__label" for="weight">Waga (kg):</label>
        <input class="form__input" type="number" placeholder="0.00" step="0.01" name="weight" id="weight" required v-model.lazy="motorcycle.weight">
      </p>
      <p class="form__errors" v-if="errors.length">
        <span>Popraw następujące błędy:</span>
        <ul>
          <li v-for="error in errors" :key="error">
            {{error}}
          </li>
        </ul>
      </p>
      <div class="buttons-wrapper">
        <button class="form__send-button join" @click.prevent="put"><font-awesome-icon class="plus-icon icon" icon="check" />Zatwierdź</button>
        <button v-if="mayBeRemoved" class="form__send-button delete" @click.prevent="deleteMotFromCatalogue"><font-awesome-icon class="plus-icon icon" icon="trash-alt" />Usuń</button>
      </div>
    </form>

    <div class="after-post" v-if="submitted">
      <h2 class="after-post__title">Model został zmodyfikowany
        <span class="after-post__title--pulse">
            <font-awesome-icon
              class="after-post__title--icon"
              icon="check"
            />
          </span>
      </h2>
    </div>

    <InfoModal
      v-if="showModal"
      :headerText="modalHeaderContent"
      :bodyText="modalBodyContent"
      @confirm="close"
    />
    <ActionModal
      v-if="showActionModal"
      :headerText="modalHeaderContent"
      :bodyText="modalBodyContent"
      @action="doAction"
      @cancel="close"
    />
  </div>
</template>

<script>
import { motorcycleUrl, motorcycleDetailsUrl } from "@/variables";
import InfoModal from "@/components/modal/InfoModal";
import ActionModal from "@/components/modal/ActionModal";

export default {
  name: "editMotorcycle",
  components: {
    InfoModal,
    ActionModal
  },
  data() {
    return {
       motorcycle: {
          model: '',
          brand: '',
          classification: '',
          engineType: '',
          capacity: '',
          power: '',
          weight:'',
      },
      classifications: ['Supersport','Cross','Custom'],
      motorcycleId: this.$route.params.id,
      submitted: false,
      errors: [],
      visible: false,
      hideErrors: false,
      modalHeaderContent: "Uwaga",
      modalBodyContent: "Coś poszło nie tak, sprawdź błędy!",
      mayBeRemoved: true,
      showModal: false,
      showActionModal: false
    };
  },
  methods: {
    back(){
        this.$router.go(-1);
    },
    show() {
      this.visible = true;
    },
   checkForm() {
      this.errors = [];
      if(this.motorcycle.model && this.motorcycle.brand && this.motorcycle.classification && this.motorcycle.power && this.motorcycle.capacity && this.motorcycle.weight &&
      this.motorcycle.engineType) {
        return true;
      }
      if(!this.motorcycle.model) {
        this.errors.push('Model jest wymagany!');
      }
      if(!this.motorcycle.brand) {
        this.errors.push('Marka jest wymagana!');
      }
      if(!this.motorcycle.classification) {
        this.errors.push('Klasyfikacja jest wymagana!');
      }
      if(!this.motorcycle.capacity) {
        this.errors.push('Pojemność silnika jest wymagana!');
      }
      if(!this.motorcycle.power) {
        this.errors.push('Moc jest wymagana!');
      }
      if(!this.motorcycle.engineType) {
        this.errors.push('Rodzaj silnika jest wymagany!');
      }
      if(!this.motorcycle.weight) {
        this.errors.push('Waga motocykla jest wymagana!');
      }
    },
    put() {
      const isValid = this.checkForm();
      if(isValid) {
        this.$http.put(`${motorcycleUrl}/${this.motorcycleId}`, this.motorcycle)
        .then(() => {
          this.submitted = true;
          setTimeout(() => {
            this.$router.push('/motorcycles');
          }, 2000);
        })
        .catch(() => {
           this.showModal = true;
           this.errors.push("Podałeś/aś niepoprawne dane, sprawdź ponownie!");
          })
      } else {
        this.showModal = true;
      }
    },
    doAction() {
          this.$http
          .delete(`${motorcycleUrl}/${this.motorcycleId}`)
          .then(() => {
            this.showActionModal = false;
            setTimeout(() => {
                this.$router.push("/motorcycles");
            }, 300);
          })
          .catch(() => {
            this.showActionModal = false;
            this.modalHeaderContent = "Uwaga";
            this.modalBodyContent = "Model motocykla z katalogu nie może zostać usunięty !";
            this.showInfoModal = true;
          });
    },
    deleteMotFromCatalogue() {
      this.modalHeaderContent = "Uwaga";
      this.modalBodyContent = `Czy na pewno chcesz usunąć model motocykla z katalogu ?`;
      this.showActionModal = true;
    },
    getMotorcycle() {
           this.$http.get(`${motorcycleUrl}/${this.motorcycleId}`)
          .then(response => response.json())
          .then(object => {
              const {model, brand, classification, power, capacity, engineType, weight} = object;
              this.motorcycle.model = model;
              this.motorcycle.brand = brand;
              this.motorcycle.classification = classification;
              this.motorcycle.power = power;
              this.motorcycle.capacity = capacity;
              this.motorcycle.engineType = engineType;
              this.motorcycle.weight = weight;
          })
          .catch(err => {
            console.log(err)
            })
        },
    getMotorcyclesInStock() {
      this.$http
        .get(`${motorcycleDetailsUrl}`)
        .then(response => response.json())
        .then(objects => {
          objects.forEach((el) => {
            if(el.motorcycle.id == this.motorcycleId) {
              this.mayBeRemoved = false;
            }
          })
          }
        )
        .catch(err => {
          console.log(err);
        });
    },
    close() {
      this.showModal = false;
      this.showActionModal = false;
    }
  },
  created() {
      this.getMotorcyclesInStock();
      this.getMotorcycle();
  }
};
</script>

<style lang="scss" scoped>
</style>