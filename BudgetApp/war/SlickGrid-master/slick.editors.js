/***
 * Contains basic SlickGrid editors.
 * @module Editors
 * @namespace Slick
 */

(function ($) {
  // register namespace
  $.extend(true, window, {
    "Slick": {
      "Editors": {
    	"Auto": AutoCompleteEditor,
        "Text": TextEditor,
        "Integer": IntegerEditor,
        "Date": DateEditor,
        "YesNoSelect": YesNoSelectEditor,
        "Checkbox": CheckboxEditor,
        "PercentComplete": PercentCompleteEditor,
        "LongText": LongTextEditor,
        "FloatText": FloatEditor,
        "GMemoriText": GMemoriEditor,
        "PONumberText": PONumberEditor,
        "PercentageAllocationText": PercentageAllocationEditor
      }
    }
  });
  
  function AutoCompleteEditor(args) {
	     var $input;
	     var defaultValue;
	     var scope = this;
	     var calendarOpen = false;
	     var autoTags = [];
	    
	     this.init = function () {
	       $input = $("<INPUT id='tags' class='editor-text' />");
	       $input.appendTo(args.container);
	       $input.focus().select();
	       if(args.column.name == "Project Owner"){
	    	   autoTags =  poOwners 
    	   }else{
    		   autoTags = availableTags
    	   }
	       $input.autocomplete({
	    		   source: function (request, response) {
	    		        var matches = $.map(autoTags, function (autoTags) {
	    		            if (autoTags.trim().toLowerCase().indexOf(request.term.toLowerCase()) === 0) {
	    		                return autoTags;
	    		            }
	    		        });
	    		        response(matches);
	    		    }
	        });
	     };

	     this.destroy = function () {
	         $input.remove();
	       };

	       this.focus = function () {
	         $input.focus();
	       };

	       this.getValue = function () {
	         return $input.val();
	       };

	       this.setValue = function (val) {
	         $input.val(val);
	       };

	       this.loadValue = function (item) {
	         defaultValue = item[args.column.field] || "";
	         $input.val(defaultValue);
	         $input[0].defaultValue = defaultValue;
	         $input.select();
	       };

	       this.serializeValue = function () {
	         return $input.val();
	       };

	       this.applyValue = function (item, state) {
	         item[args.column.field] = state;
	       };

	       this.isValueChanged = function () {
	         return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
	       };

	       this.validate = function () {
	         if (args.column.validator) {
	           var validationResults = args.column.validator($input.val());
	           if (!validationResults.valid) {
	             return validationResults;
	           }
	         }

	         return {
	           valid: true,
	           msg: null
	         };
	       };

	       this.init();
	   }

  function TextEditor(args) {
    var $input;
    var defaultValue;
    var scope = this;

    this.init = function () {
      $input = $("<INPUT type=text class='editor-text'/>")
          .appendTo(args.container)
          .bind("keydown.nav", function (e) {
            if (e.keyCode === $.ui.keyCode.LEFT || e.keyCode === $.ui.keyCode.RIGHT) {
              e.stopImmediatePropagation();
            }
          })
          .focus()
          .select();
    };

    this.destroy = function () {
      $input.remove();
    };

    this.focus = function () {
      $input.focus();
    };

    this.getValue = function () {
      return $input.val();
    };

    this.setValue = function (val) {
      $input.val(val);
    };

    this.loadValue = function (item) {
      defaultValue = item[args.column.field] || "";
      $input.val(defaultValue);
      $input[0].defaultValue = defaultValue;
      $input.select();
    };

    this.serializeValue = function () {
      return $input.val();
    };

    this.applyValue = function (item, state) {
      item[args.column.field] = state;
    };

    this.isValueChanged = function () {
      return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
    };

    this.validate = function () {
      if (args.column.validator) {
        var validationResults = args.column.validator($input.val());
        if (!validationResults.valid) {
          return validationResults;
        }
      }

      return {
        valid: true,
        msg: null
      };
    };

    this.init();
  }

  function IntegerEditor(args) {
    var $input;
    var defaultValue;
    var scope = this;

    this.init = function () {
      $input = $("<INPUT type=text class='editor-text' maxlength='6'/>");

      $input.bind("keydown.nav", function (e) {
        if (e.keyCode === $.ui.keyCode.LEFT || e.keyCode === $.ui.keyCode.RIGHT) {
          e.stopImmediatePropagation();
        }
      });

      $input.appendTo(args.container);
      $input.focus().select();
    };

    this.destroy = function () {
      $input.remove();
    };

    this.focus = function () {
      $input.focus();
    };

    this.loadValue = function (item) {
      defaultValue = item[args.column.field];
      $input.val(defaultValue);
      $input[0].defaultValue = defaultValue;
      $input.select();
    };

    this.serializeValue = function () {
      return parseInt($input.val(), 10) || 0;
    };

    this.applyValue = function (item, state) {
      item[args.column.field] = state;
    };

    this.isValueChanged = function () {
      return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
    };

    this.validate = function () {
      if (isNaN($input.val()) || ($input.val()) < 0 || ($input.val()) > 999999 ) {
        return {
          valid: false,
          msg: "Please enter a valid integer between 0 - 999,999"
        };
      }

      return {
        valid: true,
        msg: null
      };
    };

    this.init();
  }

  function DateEditor(args) {
    var $input;
    var defaultValue;
    var scope = this;
    var calendarOpen = false;

    this.init = function () {
      $input = $("<INPUT type=text class='editor-text'/>");
      $input.appendTo(args.container);
      $input.focus().select();
      $input.datepicker({
        showOn: "button",
        buttonImageOnly: true,
        buttonImage: "../images/calendar.gif",
        beforeShow: function () {
          calendarOpen = true
        },
        onClose: function () {
          calendarOpen = false
        }
      });
      $input.width($input.width() - 18);
    };

    this.destroy = function () {
      $.datepicker.dpDiv.stop(true, true);
      $input.datepicker("hide");
      $input.datepicker("destroy");
      $input.remove();
    };

    this.show = function () {
      if (calendarOpen) {
        $.datepicker.dpDiv.stop(true, true).show();
      }
    };

    this.hide = function () {
      if (calendarOpen) {
        $.datepicker.dpDiv.stop(true, true).hide();
      }
    };

    this.position = function (position) {
      if (!calendarOpen) {
        return;
      }
      $.datepicker.dpDiv
          .css("top", position.top + 30)
          .css("left", position.left);
    };

    this.focus = function () {
      $input.focus();
    };

    this.loadValue = function (item) {
      defaultValue = item[args.column.field];
      $input.val(defaultValue);
      $input[0].defaultValue = defaultValue;
      $input.select();
    };

    this.serializeValue = function () {
      return $input.val();
    };

    this.applyValue = function (item, state) {
      item[args.column.field] = state;
    };

    this.isValueChanged = function () {
      return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
    };

    this.validate = function () {
      return {
        valid: true,
        msg: null
      };
    };

    this.init();
  }

  function YesNoSelectEditor(args) {
    var $select;
    var defaultValue;
    var scope = this;

    this.init = function () {
      $select = $("<SELECT tabIndex='0' class='editor-yesno'><OPTION value='yes'>Yes</OPTION><OPTION value='no'>No</OPTION></SELECT>");
      $select.appendTo(args.container);
      $select.focus();
    };

    this.destroy = function () {
      $select.remove();
    };

    this.focus = function () {
      $select.focus();
    };

    this.loadValue = function (item) {
      $select.val((defaultValue = item[args.column.field]) ? "yes" : "no");
      $select.select();
    };

    this.serializeValue = function () {
      return ($select.val() == "yes");
    };

    this.applyValue = function (item, state) {
      item[args.column.field] = state;
    };

    this.isValueChanged = function () {
      return ($select.val() != defaultValue);
    };

    this.validate = function () {
      return {
        valid: true,
        msg: null
      };
    };

    this.init();
  }

  function CheckboxEditor(args) {
    var $select;
    var defaultValue;
    var scope = this;

    this.init = function () {
      $select = $("<INPUT type=checkbox value='true' class='editor-checkbox' hideFocus>");
      $select.appendTo(args.container);
      $select.focus();
    };

    this.destroy = function () {
      $select.remove();
    };

    this.focus = function () {
      $select.focus();
    };

    this.loadValue = function (item) {
      defaultValue = !!item[args.column.field];
      if (defaultValue) {
        $select.prop('checked', true);
      } else {
        $select.prop('checked', false);
      }
    };

    this.serializeValue = function () {
      return $select.prop('checked');
    };

    this.applyValue = function (item, state) {
      item[args.column.field] = state;
    };

    this.isValueChanged = function () {
      return (this.serializeValue() !== defaultValue);
    };

    this.validate = function () {
      return {
        valid: true,
        msg: null
      };
    };

    this.init();
  }

  function PercentCompleteEditor(args) {
    var $input, $picker;
    var defaultValue;
    var scope = this;

    this.init = function () {
      $input = $("<INPUT type=text class='editor-percentcomplete' />");
      $input.width($(args.container).innerWidth() - 25);
      $input.appendTo(args.container);

      $picker = $("<div class='editor-percentcomplete-picker' />").appendTo(args.container);
      $picker.append("<div class='editor-percentcomplete-helper'><div class='editor-percentcomplete-wrapper'><div class='editor-percentcomplete-slider' /><div class='editor-percentcomplete-buttons' /></div></div>");

      $picker.find(".editor-percentcomplete-buttons").append("<button val=0>Not started</button><br/><button val=50>In Progress</button><br/><button val=100>Complete</button>");

      $input.focus().select();

      $picker.find(".editor-percentcomplete-slider").slider({
        orientation: "vertical",
        range: "min",
        value: defaultValue,
        slide: function (event, ui) {
          $input.val(ui.value)
        }
      });

      $picker.find(".editor-percentcomplete-buttons button").bind("click", function (e) {
        $input.val($(this).attr("val"));
        $picker.find(".editor-percentcomplete-slider").slider("value", $(this).attr("val"));
      })
    };

    this.destroy = function () {
      $input.remove();
      $picker.remove();
    };

    this.focus = function () {
      $input.focus();
    };

    this.loadValue = function (item) {
      $input.val(defaultValue = item[args.column.field]);
      $input.select();
    };

    this.serializeValue = function () {
      return parseInt($input.val(), 10) || 0;
    };

    this.applyValue = function (item, state) {
      item[args.column.field] = state;
    };

    this.isValueChanged = function () {
      return (!($input.val() == "" && defaultValue == null)) && ((parseInt($input.val(), 10) || 0) != defaultValue);
    };

    this.validate = function () {
      if (isNaN(parseInt($input.val(), 10))) {
        return {
          valid: false,
          msg: "Please enter a valid positive number"
        };
      }

      return {
        valid: true,
        msg: null
      };
    };

    this.init();
  }

  /*
   * An example of a "detached" editor.
   * The UI is added onto document BODY and .position(), .show() and .hide() are implemented.
   * KeyDown events are also handled to provide handling for Tab, Shift-Tab, Esc and Ctrl-Enter.
   */
  function LongTextEditor(args) {
    var $input, $wrapper;
    var defaultValue;
    var scope = this;

    this.init = function () {
      var $container = $("body");

      $wrapper = $("<DIV style='z-index:10000;position:absolute;background:white;padding:5px;border:3px solid gray; -moz-border-radius:10px; border-radius:10px;'/>")
          .appendTo($container);

      $input = $("<TEXTAREA hidefocus rows=5 maxlength=140 style='backround:white;width:250px;height:80px;border:0;outline:0'>")
          .appendTo($wrapper);

      $("<DIV style='text-align:right'><BUTTON>Save</BUTTON><BUTTON>Cancel</BUTTON></DIV>")
          .appendTo($wrapper);

      $wrapper.find("button:first").bind("click", this.save);
      $wrapper.find("button:last").bind("click", this.cancel);
      $input.bind("keydown", this.handleKeyDown);

      scope.position(args.position);
     // $input.focus().select();
    };

    this.handleKeyDown = function (e) {
      if (e.which == $.ui.keyCode.ENTER && e.ctrlKey) {
            scope.save();
          } 
      else if (e.which == $.ui.keyCode.ENTER) {
            return false;
      } else if (e.which == $.ui.keyCode.ESCAPE) {
        e.preventDefault();
        scope.cancel();
      } else if (e.which == $.ui.keyCode.TAB && e.shiftKey) {
        e.preventDefault();
        args.grid.navigatePrev();
      } else if (e.which == $.ui.keyCode.TAB) {
        e.preventDefault();
        args.grid.navigateNext();
      }
    };

    this.save = function () {
    	if($input.val().trim().length == 0){
    	$input.val("");
    	}
      args.commitChanges();
    };

    this.cancel = function () {
      args.cancelChanges();
    };

    this.hide = function () {
      $wrapper.hide();
    };

    this.show = function () {
      $wrapper.show();
    };

    this.position = function (position) {
      $wrapper
          .css("top", position.top - 5)
          .css("left", position.left - 5)
    };

    this.destroy = function () {
      $wrapper.remove();
    };

    this.focus = function () {
      $input.focus();
    };

    this.loadValue = function (item) {
      $input.val(defaultValue = item[args.column.field]);
     // $input.select();
    };

    this.serializeValue = function () {
      return $input.val();
    };

    this.applyValue = function (item, state) {
      item[args.column.field] = state;
    };

    this.isValueChanged = function () {
      return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
    };

    this.validate = function () {
      return {
        valid: true,
        msg: null
      };
    };

    this.init();
  }
  function FloatEditor(args) {
	      var $input;
	      var defaultValue;
	      var scope = this;
	  
	      this.init = function () {
	        $input = $("<INPUT type=text class='editor-text' size='9' maxlength='9'/>");
	        $input.bind("keydown.nav", function (e) {
	          if (e.keyCode === $.ui.keyCode.LEFT || e.keyCode === $.ui.keyCode.RIGHT) {
	            e.stopImmediatePropagation();
	          }
	        });
	  
	        $input.appendTo(args.container);
	        $input.focus().select();
	      };
	  
	      this.destroy = function () {
	        $input.remove();
	      };
	  
	      this.focus = function () {
	        $input.focus();
	      };
	  
	      this.loadValue = function (item) {
	        defaultValue = item[args.column.field];
	        $input.val(defaultValue);
	        $input[0].defaultValue = defaultValue;
	        $input.select();
	      };
	  
	      this.serializeValue = function () {
	        return parseFloat($input.val().trim()) || 0;
	      };
	  
	      this.applyValue = function (item, state) {
	        item[args.column.field] = state;
	      };
	  
	      this.isValueChanged = function () {
	        return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
	      };
	  
	      this.validate = function () {
	        if (isNaN($input.val())) {
	        	$input.val(defaultValue)
	          return {
	            valid: false,
	            msg: "Please enter a valid decimal number."
	          };
	        }
	  
	        return {
	          valid: true,
	          msg: null
	        };
	      };
	  
	      this.init();
	    }
  function GMemoriEditor(args) {
	    var $input;
	    var defaultValue;
	    var scope = this;

	    this.init = function () {
	      $input = $("<INPUT type=text class='editor-text' maxlength='6'/>");

	      $input.bind("keydown.nav", function (e) {
	        if (e.keyCode === $.ui.keyCode.LEFT || e.keyCode === $.ui.keyCode.RIGHT) {
	          e.stopImmediatePropagation();
	        }
	      });

	      $input.appendTo(args.container);
	      $input.focus().select();
	    };

	    this.destroy = function () {
	      $input.remove();
	    };

	    this.focus = function () {
	      $input.focus();
	    };

	    this.loadValue = function (item) {
	      if(item[args.column.field] == 0){
	    	  defaultValue = "";
	      }else{
	    	  defaultValue = item[args.column.field];
	      }
	      $input.val(defaultValue);
	      $input[0].defaultValue = defaultValue;
	      $input.select();
	    };

	    this.serializeValue = function () {
	      return parseInt($input.val().trim()) || 0;
	    };

	    this.applyValue = function (item, state) {
	      item[args.column.field] = state;
	    };

	    this.isValueChanged = function () {
	      return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
	    };

	    this.validate = function () {
	      if ( ($input.val() != "") && (isNaN($input.val()) ||  $input.val().trim().length < 6 ||  $input.val().indexOf(".") != -1)) {
	        return {
	          valid: false,
	          msg: "Please enter 6 digit integer."
	        };
	      }

	      return {
	        valid: true,
	        msg: null
	      };
	    };

	    this.init();
	  }
  
  function PONumberEditor(args) {
	    var $input;
	    var defaultValue;
	    var scope = this;

	    this.init = function () {
	      $input = $("<INPUT type=text class='editor-text' maxlength='12' />");

	      $input.bind("keydown.nav", function (e) {
	        if (e.keyCode === $.ui.keyCode.LEFT || e.keyCode === $.ui.keyCode.RIGHT) {
	          e.stopImmediatePropagation();
	        }
	      });

	      $input.appendTo(args.container);
	      $input.focus().select();
	    };

	    this.destroy = function () {
	      $input.remove();
	    };

	    this.focus = function () {
	      $input.focus();
	    };

	    this.loadValue = function (item) {
	      defaultValue = item[args.column.field];
	      $input.val(defaultValue);
	      $input[0].defaultValue = defaultValue;
	      $input.select();
	    };

	    this.serializeValue = function () {
	      return parseInt($input.val(), 10) || 0;
	    };

	    this.applyValue = function (item, state) {
	      item[args.column.field] = state;
	    };

	    this.isValueChanged = function () {
	      return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
	    };

	    this.validate = function () {
	    	 if ( ($input.val() != "") && (isNaN($input.val()) ||  $input.val().indexOf(".") != -1)) {
	        return {
	          valid: false,
	          msg: "Please enter a valid integer"
	        };
	      }

	      return {
	        valid: true,
	        msg: null
	      };
	    };

	    this.init();
	  }
  function PercentageAllocationEditor(args) {
      var $input;
      var defaultValue;
      var scope = this;
  
      this.init = function () {
        $input = $("<INPUT type=text class='editor-text' size='9' maxlength='5'/>");
        $input.bind("keydown.nav", function (e) {
          if (e.keyCode === $.ui.keyCode.LEFT || e.keyCode === $.ui.keyCode.RIGHT) {
            e.stopImmediatePropagation();
          }
        });
  
        $input.appendTo(args.container);
        $input.focus().select();
      };
  
      this.destroy = function () {
        $input.remove();
      };
  
      this.focus = function () {
        $input.focus();
      };
  
      this.loadValue = function (item) {
        defaultValue = item[args.column.field];
        $input.val(defaultValue);
        $input[0].defaultValue = defaultValue;
        $input.select();
      };
  
      this.serializeValue = function () {
        return parseFloat($input.val(), 10) || 0;
      };
  
      this.applyValue = function (item, state) {
        item[args.column.field] = state;
      };
  
      this.isValueChanged = function () {
        return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
      };
  
      this.validate = function () {
        if (isNaN($input.val())) {
          return {
            valid: false,
            msg: "Please enter a valid decimal number"
          };
        }
  
        return {
          valid: true,
          msg: null
        };
      };
  
      this.init();
    }
})(jQuery);
