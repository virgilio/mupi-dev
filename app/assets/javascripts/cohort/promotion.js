jQuery(function() {


  Cohorts.Options.debug = true;
  var promotion = new Cohorts.Test({
    name: 'promotion_page_interaction',
    sample: 1, // we want to include all visitors in the test
    cohorts: {
      subscribe:{},
      subscribe_B:{}
    },
    storageAdapter: {
      nameSpace: 'promotion',
      trackEvent: function(category, action, opt_label, opt_value) {
        console.log('GA trackEvent: ' + category + ', ' + action + ', ' + opt_label + ', ' + opt_value);

        if(window['_gaq']) {
          _gaq.push(['_trackEvent', category, action, opt_label, opt_value]);
        } else {
          throw(" _gaq object not found: It looks like you haven't correctly setup the asynchronous Google Analytics tracking code, and you are using the default GoogleAnalyticsAdapter.");
        }
      },
      onEvent: function(testName, cohort, eventName) {
        this.trackEvent(this.nameSpace, testName, eventName);
      },
      onInitialize: function(inTest, testName, cohort) {
      }
    }
  });

  jQuery('.subscription_button').click(function() {
    promotion.event('subscription_click');
  });

  /**
   * Detecting when the comments div appears to a user
   */
  var el = jQuery("#promotion_comments")[0].getBoundingClientRect();
  if(el.top >= 0 && el.left >= 0 && el.top <= window.innerHeight && el.left <= window.innerWidth ){
    promotion.event('comments_view');
  } else{
    jQuery("#promotion_comments").bind('inview', function (event, visible) {
      if (visible == true) {
        promotion.event('comments_view');
        jQuery(this).unbind('inview');
      }
    });
  }



  promotion.setCohort('subscribe')
});
