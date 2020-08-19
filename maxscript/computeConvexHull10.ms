-- computeConvexHull v1.0: Computes a convex hull from a mesh
-- For 3dsmax 2014 - 2017
-- (c) 2016 Martin Breidt (martin@breidt.net)
--
-- This code is released under "Quote ware" license:
--      If you use this tool in a production environment with a group of more than two people,
--      or have used it in the past under such conditions, then you are obliged to tell 
--      me (martin@breidt.net) about it and allow me to list that project title and your 
--      company name as a reference on my website http://scripts.breidt.net
--
-- Just drag-and-drop this script into 3ds Max to open the tool. GUI and tooltips should be self-explanatory.
--
-- Last modified: 13.10.16

(
	fn computeConvexHull theNode maxVerts:64 pushAmt:0.0 cleanup:true = (
		with redraw off (
			local rb = MassFX_RBody()
			select theNode
			local cmdMode = getCommandPanelTaskMode()		-- store previous mode
			setCommandPanelTaskMode #modify
			addModifier theNode rb
			rb.meshVerticesLimit  = maxVerts
			rb.meshType = 4  		-- 3 = capsule, 4 = convex
			rb.meshInflation = pushAmt
			rb.RBMeshRegenerate 1
			hull = rb.RBMeshConvertToCustomMesh 1
			if isValidNode hull then (
				hull.name += "_Hull"
			-- RBMeshCustomNodeExtract 
			)
			if cleanup then (
				deleteModifier theNode 1
				setCommandPanelTaskMode cmdMode		-- restore previous mode
			)
		)
		hull
	)
	
	rollout hullGUI "Convex Hull Generator v1.0" (
		
		fn geomfilter obj = canConvertTo obj Editable_mesh		-- only allow mesh-based objects
		
		label l1 "Mesh: " pos:[6,8]
		pickbutton nodeBtn "<none>" width:150 tooltip:"Click to select scene object for which you want to compute a convex hull mesh" pos:[40,5] filter:geomfilter
		label l2 "00" pos:[200,8] tooltip:"Vertex Count"
		spinner maxVertSpn "Max. Verts " range:[16,512,64] fieldwidth:60 type:#integer tooltip:"Maximum number of vertices to use for convex hull" offset:[-30,0]
		spinner offsetSpn "Offset " range:[-1e10,1e10,0] fieldwidth:60 type:#worldunits  tooltip:"Offset of convex hull to surface" offset:[-30,0]
		button goBtn "Compute Convex Hull" width:150 enabled:false tooltip:"Click to generate convex hull" 
		label l3 ""
		button closeBtn "Close" width:150 tooltip:"Close dialog"
		
		on nodeBtn picked obj do (
			if isValidNode obj then (
				nodeBtn.text = obj.name
				l2.text = ((getNumVerts obj.mesh) as string)
				goBtn.enabled = true
			)
		)
		
		on goBtn pressed do (
			n = nodeBtn.object 
			if isValidNode n then ( 
				setWaitCursor()
				res = computeConvexHull n maxVerts:maxVertSpn.value pushAmt:offsetSpn.value
				setArrowCursor()
				if isValidNode res then (
					select res
				)
			)
		)
		
		on closeBtn pressed do destroyDialog hullGUI
	)

	createDialog hullGUI width:220

)