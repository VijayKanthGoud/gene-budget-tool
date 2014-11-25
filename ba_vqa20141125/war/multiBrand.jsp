<!DOCTYPE HTML>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

  <style>
    .slick-cell.copied {
      background: blue;
      background: rgba(0, 0, 255, 0.2);
      -webkit-transition: 0.5s background;
    }
  </style>
</head>
<body>
<div id="header" style="width:100%;height:20px; background-color:#005691; color: white">&nbsp;Multi-brand: </div>
<div id="myGrid" style="width:100%;height:230px;"></div>
<center>
<button  id="saveClose" class="myButton" value="" onclick="saveAndClose();">
		Save and close</button>
<button class="myButton" value="" onclick="saveWithoutClose();">
		Cancel</button>
 </center>

<script src="SlickGrid-master/lib/firebugx.js"></script>

<script src="SlickGrid-master/lib/jquery-1.7.min.js"></script>
<script src="SlickGrid-master/lib/jquery-ui-1.8.16.custom.min.js"></script>
<script src="SlickGrid-master/lib/jquery.event.drag-2.2.js"></script>

<script src="SlickGrid-master/slick.core.js"></script>
<script src="SlickGrid-master/plugins/slick.autotooltips.js"></script>
<script src="SlickGrid-master/plugins/slick.cellrangedecorator.js"></script>
<script src="SlickGrid-master/plugins/slick.cellrangeselector.js"></script>
<script src="SlickGrid-master/plugins/slick.cellcopymanager.js"></script>
<script src="SlickGrid-master/plugins/slick.cellselectionmodel.js"></script>
<script src="SlickGrid-master/slick.editors.js"></script>
<script src="SlickGrid-master/slick.grid.js"></script>

</body>
</html>