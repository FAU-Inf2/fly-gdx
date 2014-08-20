import math
import json
import bpy

from mathutils import Vector, Euler


class ConvertHelper:
	"""Helper class for converting blender data to export data"""
	
	@staticmethod
	def convert_pos(pos):
		"""Converts the position to floating point and cuts too many decimal places"""
		return float('%.4f' % pos)

	@staticmethod
	def convert_angle(angle):
		"""Converts the angle to degree and cuts too many decimal places"""
		return float('%.4f' % math.degrees(angle))

	@staticmethod
	def convert_id(id):
		"""Converts the object id to an integer number"""
		return int(id.replace("Gate.", ""))



class LevelExporter:
	"""Creates the dictionary of the blender data and exports it to a json file"""
	
	def __init__(self, export):
		self.data = { }
		
	def setup(self, export):
		"""Creates the dictionary of the blender data"""
		print("create level data")
	
		self.setupInfo(export)

		self.data['start'] = self.setupStartPos()
		self.data['dependencies'] = self.setupDependencies()
		self.data['gates'] = self.setupGates()
		self.data['upgrades'] = self.setupUpgrades()
		self.data['components'] = self.setupComponents(export)
		
		self.setupEnvironments()
		
	def setupInfo(self, export):
		"""Creates the level information"""		
		level = bpy.data.objects['Level']
		
		self.data['id'] = int(level['ID'])
		self.data['name'] = level['Name']
		self.data['time'] = int(level['Time'])
		self.data['class'] = level['Class']

		if len(level.game.actuators) > 0:
			if "ConstantGravity" in level.game.actuators[0].name:
				act = level.game.actuators[0]
				gravity = { }
				gravity['type'] = "ConstantGravity"
				gravity['direction'] = [ ConvertHelper.convert_pos(act.offset_location.x), ConvertHelper.convert_pos(act.offset_location.y), ConvertHelper.convert_pos(act.offset_location.z) ]
				self.data['gravity'] = gravity
				
			elif "DirectionalGravity" in level.game.actuators[0].name:
				act = level.game.actuators[0]
				gravity = { }
				gravity['type'] = "DirectionalGravity"
				gravity['position'] = [ ConvertHelper.convert_pos(act.offset_location.x), ConvertHelper.convert_pos(act.offset_location.y), ConvertHelper.convert_pos(act.offset_location.z) ]
				gravity['strength'] = ConvertHelper.convert_pos(act.force.x)
				self.data['gravity'] = gravity

	def setupEnvironments(self):
		environments = { }
		lighting = { }
		pointLights = [ ]
		dirLights = [ ]
		
		storeEnv = False
		storeLighting = False
		
		for lamp in bpy.data.lamps:
			if "Ambient" == lamp.name:
				storeEnv = True
				environments['ambient'] = [ ConvertHelper.convert_pos(lamp.color.r), ConvertHelper.convert_pos(lamp.color.g), ConvertHelper.convert_pos(lamp.color.b), 1.0 ]

			if "AmbientLight" == lamp.name:
				storeLighting = True
				lighting['ambientLight'] = [ ConvertHelper.convert_pos(lamp.color.r), ConvertHelper.convert_pos(lamp.color.g), ConvertHelper.convert_pos(lamp.color.b), 1.0 ]
				
			if "Point" in lamp.name:
				storeLighting = True
				lightObject = bpy.data.objects[lamp.name]
				
				pointLight = { }
				pointLight['color'] = [ ConvertHelper.convert_pos(lamp.color.r), ConvertHelper.convert_pos(lamp.color.g), ConvertHelper.convert_pos(lamp.color.b), 1.0 ]
				pointLight['position'] = [ ConvertHelper.convert_pos(lightObject.location.x), ConvertHelper.convert_pos(lightObject.location.y), ConvertHelper.convert_pos(lightObject.location.z) ]
				pointLight['intensity'] = lamp.energy
				pointLights.append(pointLight)
				
			if "Spot" in lamp.name:
				storeLighting = True
				lightObject = bpy.data.objects[lamp.name]
				
				dirLight = { }
				dirLight['color'] = [ ConvertHelper.convert_pos(lamp.color.r), ConvertHelper.convert_pos(lamp.color.g), ConvertHelper.convert_pos(lamp.color.b), 1.0 ]
				"""dirLight['direction'] = [ ConvertHelper.convert_pos(lightObject.location.x), ConvertHelper.convert_pos(lightObject.location.y), ConvertHelper.convert_pos(lightObject.location.z) ]"""
				
				dir = Vector((0.0, 0.0, -1.0))
				dir.rotate(lightObject.rotation_euler)
				dirLight['direction'] = [ ConvertHelper.convert_pos(dir.x), ConvertHelper.convert_pos(dir.y), ConvertHelper.convert_pos(dir.z),]
				dirLights.append(dirLight)
				
		if storeLighting:
			lighting['pointLights'] = pointLights
			lighting['directionalLights'] = dirLights
			environments['lighting'] = lighting
		
		if storeEnv:
			self.data['environments'] = environments
		
	def setupStartPos(self):
		"""Creates the starting position information"""
		player = bpy.data.objects["Player"]
    
		viewDir = Vector((0.0, 0.0, -1.0))
		viewDir.rotate(player.rotation_euler)
		
		upDir = Vector((0.0, 1.0, 0.0))
		upDir.rotate(player.rotation_euler)
		
		start = { }
		start['position'] = { 'x': ConvertHelper.convert_pos(player.location.x), 'y': ConvertHelper.convert_pos(player.location.y), 'z': ConvertHelper.convert_pos(player.location.z) }
		start['viewDirection'] = { 'x': ConvertHelper.convert_pos(viewDir.x), 'y': ConvertHelper.convert_pos(viewDir.y), 'z': ConvertHelper.convert_pos(viewDir.z) }
		start['upDirection'] = { 'x': ConvertHelper.convert_pos(upDir.x), 'y': ConvertHelper.convert_pos(upDir.y), 'z': ConvertHelper.convert_pos(upDir.z) }
		return start
		
	def setupDependencies(self):
		"""Creates the dependency information"""
		deps = { }
		for model in bpy.data.objects["Player"].keys():
			if model not in '_RNA_UI':
				deps[model] = bpy.data.objects["Player"][model]

		return deps
		
	def setupGates(self):
		"""Creates the gate information"""
		gates = [ ]

		dummy_gate = { }
		dummy_gate['successors'] = [ ]
		for constraint in bpy.data.objects["Player"].constraints:
			dummy_gate['successors'].append(ConvertHelper.convert_id(constraint.target.name))
		gates.append(dummy_gate)
		
		for item in bpy.data.objects:
			if "Gate." in item.name:
				gate = { }
				gate['gateId'] = ConvertHelper.convert_id(item.name)
				gate['refHole'] = item['HoleModel']

				successors = [ ]
				for constraint in item.constraints:
					successors.append(ConvertHelper.convert_id(constraint.target.name))

				gate['successors'] = successors
				
				self.addInfo(gate, item)
				gates.append(gate)
		
		return gates
	
	def setupUpgrades(self):
		"""Creates the upgrade information"""
		upgrades = [ ]
		
		for item in bpy.data.objects:
			up = { }
			if "changePointsUpgrade" in item.name:
				up['type'] = "ChangePointsUpgrade"
				up['points'] = item['points']
				self.addInfo(up, item)
				upgrades.append(up)
			elif "changeSteeringUpgrade" in item.name:
				up['type'] = "ChangeSteeringUpgrade"
				up['duration'] = item['duration']
				up['azimuth'] = item['azimuth']
				up['roll'] = item['roll']
				self.addInfo(up, item)
				upgrades.append(up)
			elif "changeTimeUpgrade" in item.name:
				up['type'] = "ChangeTimeUpgrade"
				up['time'] = item['time']
				self.addInfo(up, item)
				upgrades.append(up)
			elif "instantSpeedUpgrade" in item.name:
				up['type'] = "InstantSpeedUpgrade"
				up['duration'] = item['duration']
				up['speedFactor'] = item['speedFactor']
				self.addInfo(up, item)
				upgrades.append(up)
			elif "linearSpeedUpgrade" in item.name:
				up['type'] = "LinearSpeedUpgrade"
				up['increaseFactor'] = item['increaseFactor']
				up['increaseDuration'] = item['increaseDuration']
				up['decreaseFactor'] = item['decreaseFactor']
				self.addInfo(up, item)
				upgrades.append(up)
			elif "resizeGatesUpgrade" in item.name:
				up['type'] = "ResizeGatesUpgrade"
				up['scale'] = [ ConvertHelper.convert_pos(item['scaleX']), ConvertHelper.convert_pos(item['scaleY']), ConvertHelper.convert_pos(item['scaleZ']) ]
				self.addInfo(up, item)
				upgrades.append(up)

		return upgrades
	
	def addInfo(self, component, item):
		"""Adds the 3D component info"""
		
		component['id'] = item.name
		component['ref'] = item['Model']
		component['position'] = [ ConvertHelper.convert_pos(item.location.x), ConvertHelper.convert_pos(item.location.y), ConvertHelper.convert_pos(item.location.z) ]
		component['euler'] = [ ConvertHelper.convert_angle(item.rotation_euler.x) - 90.0, ConvertHelper.convert_angle(item.rotation_euler.y), ConvertHelper.convert_angle(item.rotation_euler.z) ]
		if item.scale.x != 1.0 or item.scale.y != 1.0 or item.scale.z != 1.0:
			component['scale'] = [ ConvertHelper.convert_pos(item.scale.x), ConvertHelper.convert_pos(item.scale.y), ConvertHelper.convert_pos(item.scale.z) ]
			
		if "Environment" in item:
			component['environment'] = item['Environment']

		if "Visible" in item:
			if "false" in item['Visible']:
				component['visible'] = False

		if len(item.game.actuators) > 0:
			if "Motion" in item.game.actuators[0].name:
				act = item.game.actuators[0]

				self.addIfNotZero(component, 'sinus_x', act.force)
				self.addIfNotZero(component, 'sinus_y', act.torque)
				self.addIfNotZero(component, 'sinus_z', act.linear_velocity)
				self.addIfNotZero(component, 'angular_velocity', act.angular_velocity)
		
		return component
		
	def addIfNotZero(self, component, id, value):
		if value.x != 0.0 or value.y != 0.0 or value.z != 0.0:
			component[id] = [ value.x, value.y, value.z ]
		
	def setupComponents(self, export):
		"""Creates the component information"""
		components = [ ]
		
		component = { }
		component['id'] = "space"
		component['ref'] = bpy.data.objects['Level']['Border']
		components.append(component)

		for item in bpy.data.objects:
			if "Type" in item and "deco" in item['Type']:
				component = { }
				self.addInfo(component, item)						
				components.append(component)
			
		return components

	def writeLevel(self, filepath):
		"""Exports the file to the specified filepath"""
		print("write level")
	
		f = open(filepath, 'w', encoding='utf-8')
		f.write(json.dumps(self.data, sort_keys=True, indent=4, separators=(',', ': ')))
		f.close()



def write_level(context, export):
	levelExporter = LevelExporter(export)
	
	levelExporter.setup(export)
	
	levelExporter.writeLevel(export.filepath)

	return {'FINISHED'}


from bpy_extras.io_utils import ExportHelper
from bpy.props import StringProperty, BoolProperty, EnumProperty
from bpy.types import Operator


class ExportLevelOperator(Operator, ExportHelper):
	bl_idname = "level_export.invoke"
	bl_label = "Export Fly Level"

	filename_ext = ".json"
	filter_glob = StringProperty(default="*.json", options={'HIDDEN'})

	def execute(self, context):
		return write_level(context, self)

def menu_func_export(self, context):	
	self.layout.operator(ExportLevelOperator.bl_idname, text="FLY Exporter (.json)")


def register():
	bpy.utils.register_class(ExportLevelOperator)
	bpy.types.INFO_MT_file_export.append(menu_func_export)


def unregister():
	bpy.utils.unregister_class(ExportLevelOperator)
	bpy.types.INFO_MT_file_export.remove(menu_func_export)


if __name__ == "__main__":
	register()

	bpy.ops.level_export.invoke('INVOKE_DEFAULT')