new Vue({
  el: '#app',
  data: {
    message: 'Hello World',
    contentName: '',
    publisher: ''
  },
  methods: {
    post: function () {
      console.log(this.publisher);
      axios.post('/content', {
          name: this.contentName,
          publisher: this.publisher
        })
        .then(function (response) {
          console.log(response);

        })
        .catch(function (error) {
          console.log(error);
        });

    }
  }
})
new Vue({
  el: '#app2',
  data() {
    return {
      contents: []
    }
  },
  created() {
    this.$nextTick(function () {
      this.getData()
    })
  },
  methods: {
    getData: function () {
      const self = this
      axios.get('/contents')
        .then(function (response) {
          self.contents = response.data
        })
        .catch(function (error) {
          console.log(error);
        });
    }
  }
})