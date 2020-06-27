-- Instance Materials by name and Class
-- By Alex Kramer (alex@3d-kstudio.com) www.3d-kstudio.com

try(MatInstanceForm.Close())catch()
	(
-- 	sst = timestamp()
	local ListViewInstance
	local materialsArray,MatClasses,matArray,matClassArray,DuplicateMatNameArray,mapsArray,MapClasses,mapArray,mapClassArray,DuplicateMapNameArray

	fn notLoadAssembly dotNetName = try(classof (dotNet.getType dotNetName)!=dotNetObject)catch(false)

	if (maxVersion())[1]>9500 and notLoadAssembly "MaxCustomControls.MaxForm" do dotnet.loadassembly (getdir #maxroot +"\\MaxCustomControls.dll")
	if notLoadAssembly "System.Windows.Forms.Form" do dotnet.loadassembly "System.Windows.Forms.dll"

	local instancedViewName = "Instanced Materials"

	fn CreateListViewClass =
	  (
		if classof (dotNet.getType "Kstudio.Forms.ListViewEx") != dotNetObject  then
		(
		local	source = "
	using System;
	using System.Drawing;
	using System.Windows.Forms;

	namespace Kstudio.Forms
	{
		public class ListViewEx : ListView
		{
			public ListViewEx()
				: base()
			{
			}

			protected override void OnColumnClick(ColumnClickEventArgs e)
			{
				base.OnColumnClick(e);
				this.ListViewItemSorter = new ListViewItemComparer(e.Column);
			}


			protected override void OnClientSizeChanged(EventArgs e)
			{
				base.OnClientSizeChanged(e);
				int columns_width = 2;
				for (int i = 1; i < this.Columns.Count; i++)
				{
					columns_width += this.Columns[i].Width;
				}
				this.Columns[0].Width = this.ClientSize.Width - columns_width;
			}
		}

		class ListViewItemComparer : System.Collections.IComparer
		{
			private int col;
			public ListViewItemComparer()
			{
				col = 0;
			}
			public ListViewItemComparer(int column)
			{
				col = column;
			}
			public int Compare(object x, object y)
			{
				return String.Compare(((ListViewItem)x).SubItems[col].Text, ((ListViewItem)y).SubItems[col].Text);
			}
		}
	}"

			local csharpProvider = dotnetobject "Microsoft.CSharp.CSharpCodeProvider"
			local compilerParams = dotnetobject "System.CodeDom.Compiler.CompilerParameters"
			compilerParams.ReferencedAssemblies.Add("System.dll");
			compilerParams.ReferencedAssemblies.Add("System.Drawing.dll");
			compilerParams.ReferencedAssemblies.Add("System.Windows.Forms.dll");
			compilerParams.GenerateInMemory = on
			local compilerResults = csharpProvider.CompileAssemblyFromSource compilerParams #(source)

			return compilerResults.CompiledAssembly.CreateInstance "Kstudio.Forms.ListViewEx"
		)
		else return (dotNetObject "Kstudio.Forms.ListViewEx")
	  )

	--	SORT MATERIALS BY NAME
	fn compareFN v1 v2 =
		(
		case of
			(
			(toLower (v1.Name) < toLower (v2.Name)):  -1
			(toLower (v1.Name)> toLower (v2.Name)): 1
			default: 0
			)
		)

	--	FIND MATERIALS & MAPS
	fn submats mat =
		(
			if superClassOf mat ==Material then
				appendIfUnique materialsArray mat
			else
				appendIfUnique mapsArray mat

			local sub_mat_count = try( getNumSubMtls mat)catch(0)
			if sub_mat_count > 0 do(
				local the_submat
				for i in 1 to sub_mat_count where
					(the_submat = getSubMtl mat i) != undefined do
						submats the_submat
				)

			local sub_map_count = try(getNumSubTexmaps mat)catch(0)
				if sub_map_count > 0 do(
				local the_submap
				for i in 1 to sub_map_count where
					(the_submap = getSubTexmap mat i) != undefined do
						submats the_submap
			)
		)

	--	SORT MATERIALS AND MAPS BY CLASS
	fn  getMatMapClasses MatMapClasses MatsMapsArray &MatMapArray =
		(
			for mat in MatsMapsArray where findItem MatMapClasses (classof mat)==0 do append MatMapClasses (classof mat)
			for matClass in MatMapClasses do (
				matClassArray=#()
				for mat in MatsMapsArray where findItem matClassArray mat==0 and classof mat == matClass do(
					append matClassArray mat
				)
				if matClassArray.Count>1 do(
					qsort matClassArray compareFN
					append MatMapArray matClassArray
				)
			)
		)

	--	FIND MATERIALS AND MAPS WITH NOT UNIQUE NAMES
	fn FindDuplicateMatsMaps MatsMapsArray DuplicateMatMapNameArray =
		(
		for arr in MatsMapsArray do (
				matNamesArray = for mat in arr collect mat.Name
				local notUniqueMats = #()
				for i in matNamesArray.Count to 1 by-1 do(
					if (findItem matNamesArray matNamesArray[i]) != i then (
							appendIfUnique notUniqueMats arr[i]
					)
					else (
						if notUniqueMats[1]!=undefined and notUniqueMats[1].Name == matNamesArray[i] do appendIfUnique notUniqueMats arr[i]

						if notUniqueMats.Count>0 do append DuplicateMatMapNameArray notUniqueMats
						notUniqueMats = #()
					)
				)
			)
		)

-- struct PlaceMedit
(
	fn isSmeEditor =
	(
		classof SME != UndefinedClass and MatEditor.mode == #advanced
	)

	fn DeleteInstancedView =
	(
		if classof SME != UndefinedClass and MatEditor.mode ==#advanced do(
			local instancedViewIndex = sme.GetViewByName instancedViewName
			if instancedViewIndex > 0 do
				sme.DeleteView instancedViewIndex false
		)
	)

	fn getSmeNodePosition smeNode_pos_x smeNode_pos_y NodeDistance =
	(
	local smeNode,SMESubAnim

	SMESubAnim=undefined
	smeView=sme.GetView sme.activeView
	for i in 1 to rootScene[#SME].numSubs where (getSubAnim rootScene[#SME] i).name==smeView.name do SMESubAnim = (getSubAnim rootScene[#SME] i)
	if SMESubAnim!=undefined then(
			for i in 1 to SMESubAnim.numSubs do try(
				smeNode = smeView.GetNodeByRef (getSubAnim SMESubAnim i).Properties.reference
				append smeNode_pos_x smeNode.position[1]
				append smeNode_pos_y smeNode.position[2]
			)catch(format "%\n"  (getCurrentException()) )
		if smeNode!=undefined do return [(amax smeNode_pos_x),(amax smeNode_pos_y)+NodeDistance]
		)
	return [0,0]
	)

	fn MatMapPlaceToEditor matMapArray NodeDistance:350 =
	(
		local smeNode_pos_x=#()
		local smeNode_pos_y=#()

		local n = 1
		MatEditor.Close()
		if matMapArray.Count > 24 and classof SME != UndefinedClass do
			MatEditor.mode = #advanced

		for i in 1 to matMapArray.count do
		(
			if (isSmeEditor()) then (
				try(
					local smeView, newNodePos

					smeView=sme.GetView sme.activeView
					newNodePos=getSmeNodePosition smeNode_pos_x smeNode_pos_y NodeDistance
					smeView.CreateNode matMapArray[i] newNodePos
				)catch()
			)else (
					if n<=24 do setMeditMaterial n matMapArray[i]
					n+=1
			)
		)
		if not isSmeEditor() then
			activeMeditSlot = 1
	)

	fn PlaceMeditMatsMaps e =
	(
		local matArray = #()
		for i in 0 to ListViewInstance.SelectedItems.Count-1 do
			join matArray ListViewInstance.SelectedItems.Item[i].Tag.Value
		if matArray.Count == 0 then
			messageBox "Please select instanced Materials in list"
		else
		(
			if (isSmeEditor()) do
			(
				local instancedViewIndex
				DeleteInstancedView()

				instancedViewIndex = sme.CreateView instancedViewName
				sme.activeView = instancedViewIndex
			)

			MatMapPlaceToEditor matArray

			MatEditor.Open()
		)
	)
)

	fn Refresh =
		(
			DeleteInstancedView()

			materialsArray=#()
			MatClasses=#()

			matArray = #()
			matClassArray = #()
			DuplicateMatNameArray = #()


			mapsArray=#()
			MapClasses=#()

			mapArray = #()
			mapClassArray = #()
			DuplicateMapNameArray = #()
			local SceneMaterialsArray

-- 			sst = timestamp()
			SceneMaterialsArray = for mat in scenematerials collect mat

			for mat in meditMaterials where findItem SceneMaterialsArray mat == 0 do append SceneMaterialsArray mat
			if classof SME != UndefinedClass do
				(
					smViewCount = sme.GetNumViews()
					for i in 1 to smViewCount do
					(
						smView = sme.GetView 1
						smNodesCount = smView.GetNumNodes()
						for n in 1 to smNodesCount do try(appendIfUnique SceneMaterialsArray rootScene[#SME][smView.Name][n].Properties.reference)catch()
					)
				)

			for mat in SceneMaterialsArray where findItem matArray mat ==0 do submats mat

			getMatMapClasses MatClasses materialsArray &matArray
			getMatMapClasses MapClasses MapsArray &mapArray
-- 			format "Time: % sec\n" ((timestamp()-sst)/1000.0)

			FindDuplicateMatsMaps MatArray DuplicateMatNameArray
			FindDuplicateMatsMaps MapArray DuplicateMapNameArray

			ListViewInstance.Items.Clear()
			local ListItems = #()
			for arr in DuplicateMatNameArray do (
					li = dotNetObject "ListViewItem" arr[1].Name
					li.Name = arr[1].Name
					li.SubItems.AddRange #((classof arr[1]).LocalizedName, arr.Count as string,"")
					li.Tag = dotNetMXSValue arr
					append ListItems li
				)
			for arr in DuplicateMapNameArray do (
					li = dotNetObject "ListViewItem" arr[1].Name
					li.Name = arr[1].Name
					li.SubItems.AddRange #((classof arr[1]).LocalizedName, arr.Count as string,"")
					li.Tag = dotNetMXSValue arr
					append ListItems li
				)
			ListViewInstance.Items.AddRange ListItems

			if ListItems.Count > 0 do
				ListViewInstance.Items.Item[0].Selected = true
		)

	fn FormLoad =
	(
		Refresh()
	)

	fn FormClosing =
	(
		DeleteInstancedView()
	)


	fn InstanceMaterials =
		(
			-- Make Instance for all materials - by Thinh Pham
			--str = StringStream ""
			--format ("Make instance all % groups of materials?\n\n   Continue?") (ListViewInstance.Items.Count as string) to:str
			--if (queryBox str title:"Warning: Cannot undo!")==true then
			--(
				--MatEditor.Open()
				
				while ListViewInstance.SelectedItems.Count > 0 do
				(
					-- place first item to MatEditor
					local matArray = #()
					local listItem = ListViewInstance.SelectedItems.Item[0]
					join matArray listItem.Tag.Value
					if (isSmeEditor()) do
					(
						-- delete current view from MatEditor
						--local instancedViewIndex
						DeleteInstancedView()

						-- active new view
						local instancedViewIndex = sme.CreateView instancedViewName
						sme.activeView = instancedViewIndex
					)

					-- place materials (only one) to MatEditor
					MatMapPlaceToEditor matArray
					
					local instanceMat = if (isSmeEditor()) then sme.GetMtlInParamEditor()  else medit.GetCurMtl()

					if instanceMat != undefined then
					(
						local matArray = listItem.Tag.Value
						for mat in matArray where mat != instanceMat and mat.Name == instanceMat.Name do replaceInstances mat instanceMat
						for i =1 to 24 where meditMaterials[i] == instanceMat do
						(
							updateMTLInMedit meditMaterials[i]
						)
						listItem.Remove()
						DeleteInstancedView()
						--messagebox (((matArray.count-1) as string) + " material instanced!") title:"Instance material"
					)
				)
			--)
			--close str
			--free str
		)

	global MatInstanceForm = dotnetObject "MaxCustomControls.MaxForm"
	MatInstanceForm.Text = "Instance Materials by name and Class (1.2)"
	MatInstanceForm.StartPosition = MatInstanceForm.StartPosition.CenterScreen
	MatInstanceForm.Size=dotnetobject "System.Drawing.Size" 380 340
	MatInstanceForm.MinimumSize=dotnetobject "System.Drawing.Size" 380 340

	ListViewInstance = CreateListViewClass()
	ListViewInstance.Name = "ListViewInstance"
	ListViewInstance.Dock=ListViewInstance.Dock.Fill
	ListViewInstance.View=ListViewInstance.View.Details
	ListViewInstance.GridLines=true
	ListViewInstance.FullRowSelect=true
	ListViewInstance.HideSelection=false
	ListViewInstance.Columns.Add "Name"
	ListViewInstance.Columns.Add "Class"
	ListViewInstance.Columns.Add "Number of duplicates" 115

	PanelInstance = dotNetObject "Panel"
	PanelInstance.Size=dotnetobject "System.Drawing.Size" 380 30
	PanelInstance.Dock=PanelInstance.Dock.Bottom

	ButtonMeditPlace = dotNetObject "Button"
	ButtonMeditPlace.Size=dotnetobject "System.Drawing.Size" 124 22
	ButtonMeditPlace.Text = "Place to Mat Editor"
	ButtonMeditPlace.Location=dotnetobject "System.Drawing.Point" 65 5
	ButtonMeditPlace.Anchor=(dotnet.combineEnums ButtonMeditPlace.Anchor.Right)


	ButtonInstance = dotNetObject "Button"
	ButtonInstance.Size=dotnetobject "System.Drawing.Size" 90 22
	ButtonInstance.Text = "Make Instance"
	ButtonInstance.Anchor=(dotnet.combineEnums ButtonInstance.Anchor.Right)
	ButtonInstance.Location=dotnetobject "System.Drawing.Point" 190 5

	ButtonRefresh = dotNetObject "Button"
	ButtonRefresh.Size=dotnetobject "System.Drawing.Size" 90 22
	ButtonRefresh.Text = "Refresh"
	ButtonRefresh.Anchor=(dotnet.combineEnums ButtonRefresh.Anchor.Right)
	ButtonRefresh.Location=dotnetobject "System.Drawing.Point" 285 5

	PanelInstance.Controls.Add ButtonRefresh
	PanelInstance.Controls.Add ButtonInstance
	PanelInstance.Controls.Add ButtonMeditPlace

	MatInstanceForm.Controls.Add PanelInstance
	MatInstanceForm.Controls.Add ListViewInstance

	dotNet.addEventHandler MatInstanceForm "Load" FormLoad
	dotNet.addEventHandler MatInstanceForm "Closing" FormClosing
	dotNet.addEventHandler ButtonMeditPlace "Click" PlaceMeditMatsMaps
	dotNet.addEventHandler ButtonInstance "Click" InstanceMaterials
	dotNet.addEventHandler ButtonRefresh "Click" Refresh


	MatInstanceForm.ShowModeless()

-- 	format "Time: % sec\n" ((timestamp()-sst)/1000.0)
ok
)
/*
encryptScript (getThisScriptFilename())
