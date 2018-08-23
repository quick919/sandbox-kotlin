import SelectablePublisher from './selectable-publisher.js'
import Hub from './vue-hub.js'

export default {
  template: "#modal-template",
  mixins: [SelectablePublisher],
  data: function () {
    return {
      active: false,
      editContent: {},
      publisher: "",
      title: ""
    };
  },
  methods: {
    open: function (content) {
      this.active = true;
      this.editContent = {
        id: content.id,
        title: content.title,
        imageLink: content.imageLink,
        isbnCode: content.isbnCode,
        publisher: content.publisher
      };
    },
    close: function () {
      this.active = false;
    },
    update: function (content) {
      var self = this;
      this._post("/edit", content, function () {
        Hub.$emit("updateContent", content);
        self.close();
      }.bind(self));
    },
    del: function (content) {
      var self = this;
      this._post("/delete", content, function () {
        Hub.$emit("deleteContent", content);
        self.close();
      }.bind(self));
    },
    create: function () {
      var self = this;
      var content = {
        content: {
          title: self.title,
          imageLink: "../image/noimage.png",
          isbnCode: "",
          publisher: self.publisher
        }
      };
      this._post("/content", content, function () {
        self.close();
      }.bind(self));
    },
    _post: function (url, content, after) {
      axios.post(url, JSON.stringify(content))
        .then(function (response) {
          after();
        })
        .catch(function (error) {
          console.log(error);
        });
    }
  },
  mounted() {
    this.$nextTick(
      function () {
        Hub.$on("open-modal", this.open);
        Hub.$on("close-modal", this.close);
      }.bind(this)
    );
  }
}