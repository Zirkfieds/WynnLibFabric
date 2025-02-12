package io.github.nbcss.wynnlib.gui.widgets.criteria

import io.github.nbcss.wynnlib.data.EquipmentType
import io.github.nbcss.wynnlib.gui.TooltipScreen
import io.github.nbcss.wynnlib.gui.widgets.CheckboxWidget
import io.github.nbcss.wynnlib.i18n.Translations.UI_FILTER_ITEM_TYPE
import io.github.nbcss.wynnlib.items.equipments.Equipment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.math.floor

class ItemTypeGroup(memory: CriteriaMemory<Equipment>,
                    private val screen: TooltipScreen): TitledCriteriaGroup<Equipment>(memory) {
    companion object {
        private val RENDER = MinecraftClient.getInstance().itemRenderer
        private const val FILTER_KEY = "ITEM_TYPE"
    }
    private val checkboxes: MutableMap<EquipmentType, CheckboxWidget> = linkedMapOf()
    private val contentHeight: Int
    init {
        var index = 0
        val range = listOf(1, 42, 83)
        val types = (memory.getFilter(FILTER_KEY) as? TypeFilter)?.types
        EquipmentType.getEquipmentTypes().forEach { type ->
            val posX = range[index % range.size]
            val posY = 2 + 20 * (index / range.size)
            val name = type.formatted(Formatting.GRAY)
            checkboxes[type] = CheckboxWidget(posX, posY, name, screen,
                types?.let { type in it } ?: true)
            index += 1
        }
        val group = CheckboxWidget.Group(checkboxes.values.toSet())
        checkboxes.values.forEach { it.setGroup(group) }
        contentHeight = if (index % range.size == 0) {
            20 * (index / range.size)
        }else{
            20 * (1 + index / range.size)
        }
    }

    override fun renderContent(
        matrices: MatrixStack,
        mouseX: Int,
        mouseY: Int,
        posX: Double,
        posY: Double,
        delta: Float,
        mouseOver: Boolean
    ) {
        //println("Top: $posY")
        //println("Height: ${getHeight()}")
        val x = floor(posX).toInt()
        val y = floor(posY).toInt()
        for (entry in checkboxes.entries) {
            val widget = entry.value
            widget.updatePosition(x, y)
            widget.setIntractable(mouseOver)
            widget.render(matrices, mouseX, mouseY, delta)
            RENDER.renderInGui(entry.key.getIcon(), widget.x + 20, widget.y + 1)
        }
    }

    override fun reload(memory: CriteriaMemory<Equipment>) {
        memory.getFilter(FILTER_KEY)?.let {
            if (it is TypeFilter) {
                for (entry in checkboxes.entries) {
                    entry.value.setChecked(entry.key in it.types)
                }
            }
        }
    }

    override fun getTitle(): Text = UI_FILTER_ITEM_TYPE.formatted(Formatting.GOLD)

    override fun getContentHeight(): Int = contentHeight

    override fun onClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (checkboxes.values.any { it.mouseClicked(mouseX.toDouble(), mouseY.toDouble(), button) }){
            memory.putFilter(TypeFilter(checkboxes.entries
                .filter { it.value.isChecked() }
                .map { it.key }.toSet()))
            return true
        }
        return false
    }

    class TypeFilter(val types: Set<EquipmentType>): CriteriaMemory.Filter<Equipment> {

        override fun accept(item: Equipment): Boolean {
            return item.getType() in types
        }

        override fun getKey(): String = FILTER_KEY
    }
}